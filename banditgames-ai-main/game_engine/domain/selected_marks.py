from dataclasses import dataclass
from .mark_type import MarkType

@dataclass(frozen=True)
class SelectedMarks:
    p1: MarkType
    p2: MarkType

    def __post_init__(self):
        if self.p1 == self.p2:
            raise ValueError("Players must have distinct marks.")

    def as_tuple(self) -> tuple[str, str]:
        """
        Return ('X', 'O') or ('🐱','🐶') etc.
        Used by the game engine internally.
        """
        return self.p1.value, self.p2.value
