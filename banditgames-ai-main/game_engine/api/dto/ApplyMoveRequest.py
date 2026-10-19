
from pydantic import BaseModel
from game_engine.api.dto.MoveDTO import MoveDTO


class GameMoveRequest(BaseModel):
    game_session_id: str
    player_id: str
    move: MoveDTO
