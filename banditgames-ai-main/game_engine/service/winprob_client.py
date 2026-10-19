import os
from typing import Any, Dict, Optional

import httpx


WINPROB_URL = os.getenv("WINPROB_URL", "http://winprob_api:8004").rstrip("/")


async def fetch_win_probability(
    *,
    game_session_id: str,
    board: list[list[str]],
    player_mark: str,
    move_index: int,
    ai_difficulty: str,
    timeout_s: float = 1.5,
) -> Optional[float]:
    payload = {
        "game_session_id": game_session_id,
        "board": board,
        "player_mark": player_mark,
        "move_index": move_index,
        "ai_difficulty": ai_difficulty
    }

    try:
        async with httpx.AsyncClient(timeout=timeout_s) as client:
            r = await client.post(f"{WINPROB_URL}/predict_win_probability", json=payload)
            r.raise_for_status()
            data: Dict[str, Any] = r.json()
            prob = float(data["win_probability"])
            if prob < 0.0:
                prob = 0.0
            if prob > 1.0:
                prob = 1.0
            return prob
    except Exception:
        return None