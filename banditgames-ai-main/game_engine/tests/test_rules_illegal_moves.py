import pytest
from game_engine.domain.rules import play_move
from game_engine.domain.move import Move
from game_engine.domain.models import InvalidMoveError


def test_out_of_bounds_move_raises(state_X):
    with pytest.raises(InvalidMoveError):
        play_move(state_X, Move(row=5, col=1))


def test_occupied_cell_raises(state_X):
    board = [
        ["X", "", ""],
        ["", "", ""],
        ["", "", ""]
    ]
    state = type(state_X)(board=board, current_player="O", config=state_X.config)

    with pytest.raises(InvalidMoveError):
        play_move(state, Move(row=0, col=0))
