from game_engine.domain.rules import detect_draw


def test_draw_detection(state_X):
    board = [
        ["X", "O", "X"],
        ["O", "X", "X"],
        ["O", "X", "O"]
    ]
    assert detect_draw(board, state_X.config) is True


def test_not_draw_if_winner(state_X):
    board = [
        ["X", "X", "X"],
        ["O", "", "O"],
        ["", "", ""]
    ]
    assert detect_draw(board, state_X.config) is False
