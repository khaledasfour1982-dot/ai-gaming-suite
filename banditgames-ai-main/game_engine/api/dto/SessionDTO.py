from typing import List, Optional

from pydantic import BaseModel

from game_engine.api.dto import MoveDTO, OutcomeDTO
from game_engine.domain.AIDifficulty import AIDifficulty


class StartGameRequest(BaseModel):
    game_session_id: str
    player1_id: str
    player2_id: str | None = None
    vs_ai: bool = False
    ai_difficulty: str

class StartGameResponse(BaseModel):
    game_session_id: str
    board: list[list[str]]
    current_player: str

class GameStateDTO(BaseModel):
    board: list[list[str]]
    current_player_mark: str
    current_player_id: str
    game_status: str
    legal_moves: List[MoveDTO]
    outcome: Optional[OutcomeDTO]
    win_probability: Optional[float] = None