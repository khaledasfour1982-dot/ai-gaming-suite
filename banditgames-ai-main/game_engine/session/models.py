from dataclasses import dataclass
from typing import Optional, List

from game_engine.api.dto import OutcomeDTO
from game_engine.domain.models import GameState, GameConfig


@dataclass
class GameSession:
    session_id: str
    player1_id: str
    player2_id: Optional[str]
    vs_ai: bool
    ai_difficulty: Optional[str]

    # session-specific gameplay state
    board: List[List[str]]
    current_player: str
    game_status: str         # IN_PROGRESS | FINISHED
    outcome: Optional[OutcomeDTO]  # none if not finished
    win_probability: float | None = None