from __future__ import annotations
from typing import List, Tuple

from .models import GameState, InvalidMoveError, GameConfig, Board, Mark
from .move import Move
from .outcome import Outcome, OutcomeResult, GameStatus
from .board import in_bounds, is_cell_empty, place_mark, is_full


# -------------------------------------------------------
# 1. Compute all empty cells = legal moves
# -------------------------------------------------------
def compute_legal_moves(state: GameState) -> List[Move]:
    state.validate()
    config = state.config
    moves = []

    for r in range(config.size):
        for c in range(config.size):
            if is_cell_empty(state.board, r, c, config):
                moves.append(Move(r, c))
    return moves


# -------------------------------------------------------
# 2. Validate a single move
# -------------------------------------------------------
def validate_move(state: GameState, move: Move):
    state.validate()

    if not in_bounds(move.row, move.col, state.config):
        raise InvalidMoveError("Move out of bounds.")

    if not is_cell_empty(state.board, move.row, move.col, state.config):
        raise InvalidMoveError("Cell already occupied.")


# -------------------------------------------------------
# 3. Winner detection
#    For TicTacToe 3×3 → win_length = size = 3
#    For NxN → win_length = size
# -------------------------------------------------------
def detect_winner(board: Board, config: GameConfig) -> Mark | None:
    directions = [(1, 0), (0, 1), (1, 1), (1, -1)]
    size = config.size
    needed = size  # Always require a full-line in this engine

    for r in range(size):
        for c in range(size):
            mark = board[r][c]
            if mark == config.empty_mark:
                continue
            for dr, dc in directions:
                if _has_line(board, r, c, dr, dc, mark, needed, config):
                    return mark
    return None


def _has_line(
    board: Board,
    r: int,
    c: int,
    dr: int,
    dc: int,
    mark: Mark,
    needed: int,
    config: GameConfig,
) -> bool:
    """Check board[r][c] plus next needed-1 cells form a full line."""
    for k in range(1, needed):
        nr = r + dr * k
        nc = c + dc * k
        if not in_bounds(nr, nc, config):
            return False
        if board[nr][nc] != mark:
            return False
    return True


# -------------------------------------------------------
# 4. Draw detection
# -------------------------------------------------------
def detect_draw(board: Board, config: GameConfig) -> bool:
    if detect_winner(board, config) is not None:
        return False
    return is_full(board, config)


# -------------------------------------------------------
# 5. Player switching logic
# -------------------------------------------------------
def compute_next_player(current_player: Mark, config: GameConfig) -> Mark:
    marks = config.marks.as_tuple()
    idx = marks.index(current_player)
    return marks[(idx + 1) % 2]


# -------------------------------------------------------
# 6. Apply a move → return new state + game status + outcome + legal moves
# -------------------------------------------------------
def play_move(state: GameState, move: Move):
    validate_move(state, move)
    config = state.config

    # Apply the mark
    new_board = place_mark(
        state.board,
        move.row,
        move.col,
        state.current_player
    )

    # Check winner
    winner = detect_winner(new_board, config)
    if winner:
        new_state = GameState(new_board, state.current_player, config)
        return new_state, GameStatus.FINISHED, Outcome.win(winner), []

    # Check draw
    if detect_draw(new_board, config):
        new_state = GameState(new_board, state.current_player, config)
        return new_state, GameStatus.FINISHED, Outcome.draw(), []

    # Game continues
    next_player = compute_next_player(state.current_player, config)
    new_state = GameState(new_board, next_player, config)
    legal_moves = compute_legal_moves(new_state)

    return new_state, GameStatus.IN_PROGRESS, Outcome.none(), legal_moves
