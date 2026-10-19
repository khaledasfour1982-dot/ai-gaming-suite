import pytest
from game_engine.domain.mark_type import MarkType
from game_engine.domain.selected_marks import SelectedMarks


def test_mark_enum_contains_default_symbols():
    assert MarkType.X.value == "X"
    assert MarkType.O.value == "O"


def test_mark_enum_contains_custom_symbols():
    # adjust if you added different emoji/symbols
    assert MarkType.CAT.value == "🐱"
    assert MarkType.DOG.value == "🐶"
    assert MarkType.STAR.value == "⭐"
    assert MarkType.FIRE.value == "🔥"


def test_selected_marks_valid_pair():
    marks = SelectedMarks(MarkType.X, MarkType.O)
    assert marks.p1 == MarkType.X
    assert marks.p2 == MarkType.O


def test_selected_marks_rejects_same_mark():
    with pytest.raises(ValueError):
        SelectedMarks(MarkType.X, MarkType.X)


def test_selected_marks_as_tuple_returns_strings():
    marks = SelectedMarks(MarkType.CAT, MarkType.DOG)
    assert marks.as_tuple() == ("🐱", "🐶")


def test_selected_marks_json_serializable():
    marks = SelectedMarks(MarkType.X, MarkType.O)
    result = marks.as_tuple()

    # Tuple of JSON-safe strings
    assert result == ("X", "O")
    assert isinstance(result[0], str)
    assert isinstance(result[1], str)
