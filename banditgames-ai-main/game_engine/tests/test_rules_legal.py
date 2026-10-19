from game_engine.domain.rules import compute_legal_moves
from game_engine.domain.models import GameState


def test_legal_moves_after_some_moves(state_X):
    board = [
        ["X", "O", "X"],
        ["", "", ""],
        ["O", "", ""]
    ]
    state = GameState(board=board, current_player="X", config=state_X.config)

    legal = compute_legal_moves(state)
    assert len(legal) == 5
