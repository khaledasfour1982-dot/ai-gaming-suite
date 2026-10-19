from fastapi import APIRouter
from game_engine.api.dto import GameMoveRequest, GameMoveResponse
from game_engine.service.game_service import GameService

router = APIRouter(prefix="/api/game", tags=["Game"])


@router.post("/move", response_model=GameMoveResponse)
async def make_move(req: GameMoveRequest) -> GameMoveResponse:
    return await GameService.process_move(req)
