from fastapi import HTTPException

from game_engine.domain.models import GameState
from game_engine.domain.move import Move
from game_engine.domain.rules import play_move
from game_engine.aiCommunication.AgentEngine import call_external_ai
from game_engine.api.dto import OutcomeDTO, MoveDTO, GameMoveResponse
from game_engine.service.achievements import AchievementEvaluator
from game_engine.session.session_manager import (
    get_session,
    save_session,
    get_current_player_id,
)
from game_engine.config.settings import SETTINGS
from game_engine.service.winprob_client import fetch_win_probability


class GameService:
    """
    Application service containing business logic for processing moves.
    """

    @staticmethod
    async def process_move(req) -> GameMoveResponse:
        """
        Handles full move flow:
        - load session
        - validate player
        - apply move
        - handle AI if needed
        - update session
        - return API DTO
        """

        session = await GameService._load_session(req.game_session_id)

        config = SETTINGS.game_config
        state = GameState(
            board=session.board,
            current_player=session.current_player,
            config=config,
        )

        GameService._validate_player(req, session, config)

        # Apply human move
        move = Move(req.move.row, req.move.col)
        new_state, status, outcome, legal_moves_after = play_move(state, move)

        if req.player_id != "AI":
            await AchievementEvaluator.evaluate_move_achievements(
                session=session,
                state_before=state,
                move=move,
                player_id=req.player_id
            )

        GameService._update_session_from_state(session, new_state, status, outcome)

        # If finished OR PvP game → return immediately
        if session.game_status == "FINISHED" or not session.vs_ai:
            if req.player_id != "AI":
                await AchievementEvaluator.evaluate_endgame_achievements(
                    session=session,
                    final_state=new_state,
                    player_id=req.player_id,
                    outcome=outcome
                )

            await save_session(session)

            winprob = await fetch_win_probability(
                game_session_id=req.game_session_id,
                board=session.board,
                player_mark=session.current_player,
                move_index=GameService._count_moves(session.board),
                ai_difficulty=session.ai_difficulty,
            )

            session.win_probability = winprob
            await save_session(session)

            return GameService._build_response(session, move, config, win_probability=winprob)

        # If no legal moves → return
        if not legal_moves_after:
            await save_session(session)

            winprob = await fetch_win_probability(
                game_session_id=req.game_session_id,
                board=session.board,
                player_mark=session.current_player,
                move_index=GameService._count_moves(session.board),
                ai_difficulty=session.ai_difficulty,
            )

            session.win_probability = winprob
            await save_session(session)

            return GameService._build_response(session, move, config, win_probability=winprob)

        # AI move
        ai_response = await call_external_ai(
            board=new_state.board,
            current_player=new_state.current_player,
            legal_moves=legal_moves_after,
            difficulty=session.ai_difficulty
        )

        ai_move = ai_response["move"]

        new_state2, status2, outcome2, legal_after_ai = play_move(new_state, ai_move)

        GameService._update_session_from_state(session, new_state2, status2, outcome2)

        await save_session(session)

        winprob = await fetch_win_probability(
            game_session_id=req.game_session_id,
            board=session.board,
            player_mark=session.current_player,
            move_index=GameService._count_moves(session.board),
            ai_difficulty=session.ai_difficulty,
        )

        session.win_probability = winprob
        await save_session(session)

        return GameService._build_response(session, ai_move, config, win_probability=winprob)

    # -------------------------------------------------------------------------
    # Helpers
    # -------------------------------------------------------------------------

    @staticmethod
    def _load_session(game_session_id):
        try:
            return get_session(game_session_id)
        except ValueError as e:
            raise HTTPException(404, str(e))

    @staticmethod
    def _validate_player(req, session, config):
        valid_players = {session.player1_id}
        if session.player2_id:
            valid_players.add(session.player2_id)

        if req.player_id not in valid_players:
            raise HTTPException(403, "Player not part of this game.")

        # PvP turn validation
        if not session.vs_ai and session.player2_id:
            expected = (
                session.player1_id
                if session.current_player == config.marks.p1.value
                else session.player2_id
            )
            if req.player_id != expected:
                raise HTTPException(409, "Not your turn.")

    @staticmethod
    def _update_session_from_state(session, new_state, status, outcome):
        session.board = new_state.board
        session.current_player = new_state.current_player
        session.game_status = status.value
        session.outcome = (
            OutcomeDTO(result=outcome.result.value, winner=outcome.winner)
            if outcome else None
        )

    @staticmethod
    def _build_response(session, move, config, win_probability=None):
        return GameMoveResponse(
            board=session.board,
            current_player_mark=session.current_player,
            current_player_id=get_current_player_id(session, config),
            last_move=MoveDTO(row=move.row, col=move.col),
            win_probability=win_probability,
        )

    @staticmethod
    def _count_moves(board: list[list[str]]) -> int:
        return sum(1 for row in board for cell in row if cell not in (None, "", " "))