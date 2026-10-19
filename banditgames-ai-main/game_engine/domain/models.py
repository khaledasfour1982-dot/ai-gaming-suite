from __future__ import annotations

from dataclasses import dataclass
from typing import List, Optional, TypeAlias

from .mark_type import MarkType
from .selected_marks import SelectedMarks


Mark: TypeAlias = str
Board: TypeAlias = List[List[Mark]]


class DomainError(Exception):
    """Base class for domain-related errors."""


class ConfigurationError(DomainError):
    """Raised when game configuration is invalid."""


class InvalidMoveError(DomainError):
    """Raised when a move is invalid with an explanation."""

    def __init__(self, message: str, details: Optional[dict] = None) -> None:
        super().__init__(message)
        self.details = details or {}


@dataclass(frozen=True)
class GameConfig:
    """Game configuration for an N x N TicTacToe-like game."""

    size: int
    marks: SelectedMarks
    empty_mark: Mark = ""

    def __post_init__(self) -> None:
        if self.size <= 0:
            raise ConfigurationError("Board size must be positive.")

        if not isinstance(self.marks, SelectedMarks):
            raise ConfigurationError("Marks must be a SelectedMarks instance.")


@dataclass(frozen=True)
class GameState:
    """Complete game state, passed in and out of the engine."""

    board: Board
    current_player: Mark
    config: GameConfig

    def validate(self) -> None:
        """Basic invariants on board and current player."""
        size = self.config.size
        if len(self.board) != size:
            raise ConfigurationError(
                f"Board has {len(self.board)} rows, expected {size}."
            )
        for r, row in enumerate(self.board):
            if len(row) != size:
                raise ConfigurationError(
                    f"Row {r} has {len(row)} columns, expected {size}."
                )
        valid_marks = {self.config.marks.p1.value, self.config.marks.p2.value}
        if self.current_player not in valid_marks:
            raise ConfigurationError(
                f"Invalid current_player '{self.current_player}', expected one of {valid_marks}"
            )


def default_config_from_board(
    board: Board,
    current_player: Mark,
) -> GameConfig:
    """
    Helper to build a default GameConfig from board + player
    for the standard 2-player TicTacToe use case.
    - size inferred from board.
    - win_length = min(3, size) by default.
    - marks = inferred from board + current_player, sorted for stability.
    """

    size = len(board)
    win_length = min(3, size) if size > 0 else 3

    seen_marks = set(
        cell
        for row in board
        for cell in row
        if cell is not None and cell != ""
    )
    if current_player not in ("", None):
        seen_marks.add(current_player)

    # Always ensure at least 2 marks
    if len(seen_marks) < 2:
        # Inject the classic defaults but keep current_player FIRST
        if current_player not in seen_marks:
            seen_marks.add(current_player)

        # Hard-default: if still only 1 mark, add a second default one
        if len(seen_marks) < 2:
            # Add 'O' if current is X, else add 'X'
            other = "O" if current_player == "X" else "X"
            seen_marks.add(other)

    marks = tuple(sorted(seen_marks))

    return GameConfig(
        size=size,
        marks=SelectedMarks(
            MarkType.X,  # default P1
            MarkType.O,  # default P2
        )
    )

