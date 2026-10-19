from datetime import datetime, timezone
from uuid import UUID
from fastapi import HTTPException
from sqlalchemy import select

from game_engine.config.settings import SETTINGS
from game_engine.infrastructure.messaging.rabbitmq import RabbitMQPublisher
from game_engine.session.models import GameSession as DomainSession


from game_engine.infrastructure.database import db

from game_engine.infrastructure.game_models import (
    GameSession as DBGameSession,
    SessionPlayer,
    GameOutcome,

)


def get_current_player_id(session, config):
    return (
        session.player1_id
        if session.current_player == config.marks.p1.value
        else session.player2_id
    )


# ---------------------------------------------------------------------
# CREATE SESSION
# ---------------------------------------------------------------------
async def create_session(domain: DomainSession):
    config = SETTINGS.game_config

    async with db.session() as s:

        # Save game row
        db_game = DBGameSession(
            id=UUID(domain.session_id),
            status=domain.game_status,
            board_state=domain.board,
            current_player=domain.current_player,
            board_size=config.size,
            player1_mark=config.marks.p1.value,
            player2_mark=config.marks.p2.value,
        )
        s.add(db_game)

        # Save Player 1 (ID from frontend)
        s.add(SessionPlayer(
            id=domain.player1_id,
            game_session_id=db_game.id,
            mark=config.marks.p1.value,
            player_type="human",
            ai_difficulty=None
        ))

        # Save Player 2 (human or AI)
        if domain.player2_id:
            s.add(SessionPlayer(
                id=domain.player2_id,
                game_session_id=db_game.id,
                mark=config.marks.p2.value,
                player_type="ai" if domain.vs_ai else "human",
                ai_difficulty=domain.ai_difficulty if domain.vs_ai else None
            ))

        #publish RabbitMQ message
        event = {
            "messageType": "GAME_CREATED",
            "aiDifficulty": domain.ai_difficulty,
            "gameSessionId": domain.session_id,
            "players": [domain.player1_id, domain.player2_id],
            "matchType": _get_match_type(domain),
            "timestamp": datetime.now(timezone.utc).isoformat()
        }

    await RabbitMQPublisher.publish("tictactoe.game.created", event)

    return domain

def _get_match_type(session: DomainSession) -> str:
    return "HUMAN_VS_AI" if session.vs_ai else "HUMAN_VS_HUMAN"


# ---------------------------------------------------------------------
# GET SESSION
# ---------------------------------------------------------------------
async def get_session(session_id: str) -> DomainSession:
    async with db.session() as s:

        # Load game session
        stmt = select(DBGameSession).where(DBGameSession.id == UUID(session_id))
        db_game = (await s.execute(stmt)).scalar_one_or_none()
        if not db_game:
            raise HTTPException(404, "Session not found")

        # Load players
        pstmt = select(SessionPlayer).where(SessionPlayer.game_session_id == db_game.id)
        players = list((await s.execute(pstmt)).scalars())

        # Extract IDs exactly as stored from frontend
        player1 = next((p for p in players if p.mark == db_game.player1_mark), None)
        player2 = next((p for p in players if p.mark == db_game.player2_mark), None)

        return DomainSession(
            session_id=session_id,
            board=db_game.board_state,
            current_player=db_game.current_player,
            game_status=db_game.status,
            player1_id=player1.id if player1 else None,
            player2_id=player2.id if player2 else None,
            vs_ai=(player2.player_type == "ai") if player2 else False,
            ai_difficulty=player2.ai_difficulty if player2 else None,
            outcome=None,
            win_probability=db_game.win_probability,
        )


# ---------------------------------------------------------------------
# SAVE SESSION
# ---------------------------------------------------------------------
async def save_session(session: DomainSession):
    async with db.session() as s:

        stmt = select(DBGameSession).where(DBGameSession.id == UUID(session.session_id))
        db_game = (await s.execute(stmt)).scalar_one()

        db_game.board_state = session.board
        db_game.status = session.game_status
        db_game.current_player = session.current_player
        db_game.win_probability = session.win_probability

        # Only publish game.ended event and save outcome if game just finished
        # Check if outcome already exists to avoid duplicate events
        if session.game_status == "FINISHED" and session.outcome:
            # Check if outcome already exists in database
            outcome_stmt = select(GameOutcome).where(
                GameOutcome.game_session_id == db_game.id
            )
            existing_outcome = (await s.execute(outcome_stmt)).scalar_one_or_none()
            
            # Only publish event and save outcome if it doesn't exist yet
            if existing_outcome is None:
                event = {
                    "gameId": session.session_id,
                    "winnerId": get_current_player_id(session, SETTINGS.game_config),
                    "winner": session.outcome.winner,
                    "result": session.outcome.result,
                    "ended_At": datetime.now(timezone.utc).isoformat()
                }

                await RabbitMQPublisher.publish("tictactoe.game.ended", event)

                s.add(GameOutcome(
                    game_session_id=db_game.id,
                    result=session.outcome.result,
                    winner_mark=session.outcome.winner
                ))
