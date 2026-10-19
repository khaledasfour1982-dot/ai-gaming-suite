from pydantic import BaseModel
from .MoveDTO import MoveDTO
from .OutcomeDTO import OutcomeDTO
from typing import Optional


class GameMoveResponse(BaseModel):
    board: list[list[str]]
    current_player_mark: str
    # current_player_id: str
    last_move: MoveDTO
    win_probability: Optional[float] = None
