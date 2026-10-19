from pydantic import BaseModel, Field

class MoveDTO(BaseModel):
    row: int = Field(..., ge=0)
    col: int = Field(..., ge=0)
