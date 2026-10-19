from game_engine.domain.rules import detect_winner
from game_engine.domain.models import GameState


def test_winner_row(state_X):
    board = [
        ["X", "X", "X"],
        ["", "", ""],
        ["", "", ""]
    ]
    state = GameState(board=board, current_player="X", config=state_X.config)
    assert detect_winner(board, state.config) == "X"


def test_winner_col(state_X):
    board = [
        ["O", "", ""],
        ["O", "", ""],
        ["O", "", ""]
    ]
    state = GameState(board=board, current_player="O", config=state_X.config)
    assert detect_winner(board, state.config) == "O"


def test_winner_diag(state_X):
    board = [
        ["X", "", ""],
        ["", "X", ""],
        ["", "", "X"]
    ]
    state = GameState(board=board, current_player="X", config=state_X.config)
    assert detect_winner(board, state.config) == "X"


def test_no_winner(state_X):
    board = [
        ["X", "O", "X"],
        ["O", "X", "O"],
        ["O", "X", "O"]
    ]
    assert detect_winner(board, state_X.config) is None
