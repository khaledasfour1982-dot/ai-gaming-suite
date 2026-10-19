from fastapi import APIRouter
from pydantic import BaseModel

from ai.service import choose_move_mcts
from game_engine.config.settings import SETTINGS
from game_engine.domain.models import GameState

router = APIRouter(prefix="/api", tags=["AI"])


class AIMoveRequest(BaseModel):
    board: list[list[str]]
    current_player: str
    legal_moves: list[dict]
    difficulty: str


@router.post("/move")
def get_ai_move(req: AIMoveRequest):
    game_state = GameState(
        board=req.board,
        current_player=req.current_player,
        config=SETTINGS.game_config,
    )

    best_move_info = choose_move_mcts(
        state=game_state,
        difficulty=req.difficulty,
    )

    return best_move_info

