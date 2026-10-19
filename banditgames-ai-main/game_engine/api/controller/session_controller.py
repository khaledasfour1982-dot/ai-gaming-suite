from fastapi import APIRouter, HTTPException

from game_engine.api.dto.SessionDTO import (
    StartGameRequest,
    StartGameResponse,
    GameStateDTO,
)
from game_engine.api.dto import MoveDTO
from game_engine.session.session_manager import (
    create_session,
    get_session,
    get_current_player_id,
)
from game_engine.domain.rules import compute_legal_moves
from game_engine.domain.models import GameState
from game_engine.config.settings import SETTINGS
from game_engine.session.models import GameSession as InMemorySession
from game_engine.service.adaptive_difficulty_service import AdaptiveDifficultyService

router = APIRouter(prefix="/api/session", tags=["Session"])


@router.post("/start", response_model=StartGameResponse)
async def start_game(req: StartGameRequest):

    # Build initial in-memory session model (the PostgreSQL layer will store it)
    config = SETTINGS.game_config

    # Determine AI difficulty: use adaptive if requested, otherwise use provided difficulty
    ai_difficulty = req.ai_difficulty
    if req.vs_ai and (ai_difficulty is None or ai_difficulty.lower() == "adaptive"):
        # Calculate adaptive difficulty based on player's win rate
        ai_difficulty = await AdaptiveDifficultyService.calculate_adaptive_difficulty(
            req.player1_id
        )

    initial_session = InMemorySession(
        session_id=req.game_session_id,
        player1_id=req.player1_id,
        player2_id=req.player2_id if not req.vs_ai else "AI",
        vs_ai=req.vs_ai,
        ai_difficulty=ai_difficulty,
        board=[[SETTINGS.game_config.empty_mark for _ in range(config.size)]
               for _ in range(config.size)],
        current_player=config.marks.p1.value,
        game_status="IN_PROGRESS",
        outcome=None,
    )

    # Persist it into PostgreSQL
    await create_session(initial_session)

    # Compute legal moves
    legal_moves = compute_legal_moves(GameState(
        board=initial_session.board,
        current_player=initial_session.current_player,
        config=config,
    ))

    return StartGameResponse(
        game_session_id=initial_session.session_id,
        board=initial_session.board,
        current_player=initial_session.current_player,
        legal_moves=[MoveDTO(row=m.row, col=m.col) for m in legal_moves]
    )


@router.get("/{game_session_id}", response_model=GameStateDTO)
async def get_state(game_session_id: str):

    try:
        session = await get_session(game_session_id)
    except HTTPException:
        raise
    except Exception:
        raise HTTPException(404, "Session not found")

    config = SETTINGS.game_config

    legal_moves = compute_legal_moves(GameState(
        board=session.board,
        current_player=session.current_player,
        config=config,
    ))

    return GameStateDTO(
        board=session.board,
        current_player_mark=session.current_player,
        current_player_id=get_current_player_id(session, config),
        game_status=session.game_status,
        legal_moves=[MoveDTO(row=m.row, col=m.col) for m in legal_moves],
        outcome=session.outcome,
        win_probability=session.win_probability,
    )
