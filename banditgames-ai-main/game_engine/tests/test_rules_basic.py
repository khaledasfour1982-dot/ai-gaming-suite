from game_engine.domain.rules import compute_legal_moves

def test_empty_board_legal_moves_count(state_X):
    legal = compute_legal_moves(state_X)
    assert len(legal) == 9
