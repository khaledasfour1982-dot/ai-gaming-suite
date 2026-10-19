from __future__ import annotations

from copy import deepcopy

from game_engine.domain.mark_type import MarkType
from game_engine.domain.models import GameConfig, Board


def create_empty_board(config: GameConfig) -> Board:
    """Create an empty board for the given configuration."""
    return [
        [config.empty_mark for _ in range(config.size)]
        for _ in range(config.size)
    ]


def in_bounds(
    row: int,
    col: int,
    config: GameConfig,
) -> bool:
    return 0 <= row < config.size and 0 <= col < config.size


def is_cell_empty(
    board: Board,
    row: int,
    col: int,
    config: GameConfig,
) -> bool:
    return board[row][col] == config.empty_mark


def place_mark(
    board: Board,
    row: int,
    col: int,
    mark: MarkType,
) -> Board:
    """
    Return a new board with the mark placed at (row, col).
    Does not validate bounds or emptiness.
    """
    new_board: Board = deepcopy(board)
    new_board[row][col] = mark
    return new_board


def is_full(board: Board, config: GameConfig) -> bool:
    """True if there are no empty cells."""
    for row in board:
        for cell in row:
            if cell == config.empty_mark:
                return False
    return True


def copy_board(board: Board) -> Board:
    """Utility for copying a board when config is unavailable."""
    return [list(row) for row in board]
