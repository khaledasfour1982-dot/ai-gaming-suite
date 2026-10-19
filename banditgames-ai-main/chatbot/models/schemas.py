from pydantic import BaseModel

class ChatRequest(BaseModel):
    message: str
    session_id: str
    game_code: str | None = None

class ChatResponse(BaseModel):
    answer: str
