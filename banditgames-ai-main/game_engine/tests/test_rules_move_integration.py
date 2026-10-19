from game_engine.domain.rules import play_move
from game_engine.domain.move import Move
from game_engine.domain.outcome import OutcomeResult, GameStatus


def test_play_move_integration_row_win(state_X):
    board = [
        ["X", "X", ""],
        ["", "", ""],
        ["", "", ""]
    ]
    state = type(state_X)(board=board, current_player="X", config=state_X.config)

    new_state, status, outcome, legal = play_move(state, Move(0, 2))

    assert status == GameStatus.FINISHED
    assert outcome.result == OutcomeResult.WIN
    assert outcome.winner == "X"
    assert len(legal) == 0
