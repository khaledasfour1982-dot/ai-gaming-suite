import pytest
from fastapi.testclient import TestClient

from game_engine.main import app
from game_engine.domain.models import GameConfig, GameState
from game_engine.domain.selected_marks import SelectedMarks
from game_engine.domain.mark_type import MarkType
from game_engine.domain.board import create_empty_board


@pytest.fixture
def client():
    return TestClient(app)


@pytest.fixture
def standard_config():
    return GameConfig(
        size=3,
        marks=SelectedMarks(MarkType.X, MarkType.O),
    )


@pytest.fixture
def empty_board(standard_config):
    return create_empty_board(standard_config)


@pytest.fixture
def state_X(empty_board, standard_config):
    return GameState(empty_board, "X", standard_config)


@pytest.fixture
def state_O(empty_board, standard_config):
    return GameState(empty_board, "O", standard_config)






