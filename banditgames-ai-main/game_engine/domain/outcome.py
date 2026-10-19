from __future__ import annotations

from dataclasses import dataclass
from enum import Enum
from typing import Optional

from .models import Mark


class GameStatus(str, Enum):
    IN_PROGRESS = "IN_PROGRESS"
    FINISHED = "FINISHED"


class OutcomeResult(str, Enum):
    WIN = "WIN"
    DRAW = "DRAW"
    NONE = "NONE"


@dataclass(frozen=True)
class Outcome:
    result: OutcomeResult
    winner: Optional[Mark] = None

    @staticmethod
    def none() -> "Outcome":
        return Outcome(result=OutcomeResult.NONE, winner=None)

    @staticmethod
    def draw() -> "Outcome":
        return Outcome(result=OutcomeResult.DRAW, winner=None)

    @staticmethod
    def win(winner: Mark) -> "Outcome":
        return Outcome(result=OutcomeResult.WIN, winner=winner)
