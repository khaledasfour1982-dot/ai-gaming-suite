from time import time

_TTL = 3600
_sessions: dict[str, tuple[float, str | None]] = {}

def get_last_game(session_id: str) -> str | None:
    ts_game = _sessions.get(session_id)
    if not ts_game:
        return None
    ts, game = ts_game
    if time() - ts > _TTL:
        _sessions.pop(session_id, None)
        return None
    return game

def set_last_game(session_id: str, game_code: str | None) -> None:
    _sessions[session_id] = (time(), game_code)
