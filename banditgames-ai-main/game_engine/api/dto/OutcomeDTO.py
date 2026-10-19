from pydantic import BaseModel
from typing import Optional, List, Dict


class OutcomeDTO(BaseModel):
    result: str              # "WIN" | "DRAW" | "NONE"
    winner: Optional[str]    # "X" | "O" | "G" | "H" | None

