from fastapi import APIRouter
from pydantic import BaseModel

from game_engine.service.ai_vs_ai_service import AiVsAiService

router = APIRouter(prefix="/api/sim", tags=["AI Simulation"])


class AiVsAiRequest(BaseModel):
    iterations: int
    x_difficulty: str
    o_difficulty: str


class AiVsAiResponse(BaseModel):
    iterations: int
    x_wins: int
    o_wins: int
    draws: int


@router.post("/simulate", response_model=AiVsAiResponse)
async def simulate_ai_vs_ai(req: AiVsAiRequest):
    result = await AiVsAiService.simulate(
        iterations=req.iterations,
        x_difficulty=req.x_difficulty,
        o_difficulty=req.o_difficulty
    )
    return result
