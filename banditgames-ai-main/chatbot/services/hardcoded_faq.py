from typing import Optional

_HARDCODED_QA: dict[tuple[str, str], str] = {
    ("", "how do i win in a chess game?"):
        "You win in chess by checkmating your opponent's king. "
        "Checkmate means the king is under attack and has no legal moves to escape.",
    ("", "how do i win in tic tac toe?"):
        "You win in Tic Tac Toe by placing three of your marks in a row, column, or diagonal.",
}

def get_hardcoded_answer(game_code: str | None, question: str) -> Optional[str]:
    key = ((game_code or "").upper(), question.strip().lower())
    return _HARDCODED_QA.get(key)
