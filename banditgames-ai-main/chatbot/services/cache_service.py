from time import time

_cache: dict[tuple[str, str], tuple[float, str]] = {}
TTL_SECONDS = 300

def get_cached_answer(game_code: str | None, question: str) -> str | None:
    key = (game_code or "", question.strip().lower())
    item = _cache.get(key)
    if not item:
        return None
    ts, answer = item
    if time() - ts > TTL_SECONDS:
        _cache.pop(key, None)
        return None
    return answer

def set_cached_answer(game_code: str | None, question: str, answer: str) -> None:
    key = (game_code or "", question.strip().lower())
    _cache[key] = (time(), answer)
