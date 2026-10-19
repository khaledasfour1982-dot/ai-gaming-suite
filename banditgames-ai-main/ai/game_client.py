import httpx


GAME_ENGINE_BASE_URL = "http://localhost:8000/api/engine"


class GameClient:
    def __init__(self):
        self._client = httpx.Client(timeout=5.0)

    def evaluate_state(self, board, current_player):
        """Calls /api/engine/state"""
        payload = {
            "board": board,
            "current_player": current_player,
        }
        resp = self._client.post(f"{GAME_ENGINE_BASE_URL}/state", json=payload)
        resp.raise_for_status()
        data = resp.json()

        return {
            "game_status": data["game_status"],
            "result": data["result"],
            "winner": data["winner"],
            "legal_moves": data["legal_moves"],
        }

    def play_move(self, board, current_player, move):
        """Calls /api/engine/play-move"""
        payload = {
            "board": board,
            "current_player": current_player,
            "move": {"row": move.row, "col": move.col},
        }
        resp = self._client.post(f"{GAME_ENGINE_BASE_URL}/play-move", json=payload)
        resp.raise_for_status()
        data = resp.json()

        return (
            data["board"],
            data["current_player"],
            {
                "game_status": data["game_status"],
                "result": data["result"],
                "winner": data["winner"],
                "legal_moves": data["legal_moves"],
            },
        )
