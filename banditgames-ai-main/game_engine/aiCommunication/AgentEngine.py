# game_engine/aiCommunication/AgentEngine.py

from typing import List
import os

import httpx

from game_engine.domain.move import Move

# In dev:
#   - game backend: http://localhost:8000
#   - AI service:   http://localhost:8000
#
# In Docker:
#   - AI service might be: http://ai-service:8000

AI_SERVICE_BASE_URL = os.getenv("AI_SERVICE_BASE_URL", "http://ttt_ai:8002")
AI_MOVE_ENDPOINT = os.getenv("AI_MOVE_ENDPOINT", "/api/move")


async def call_external_ai(board, current_player, legal_moves, difficulty: str = "medium"):
    """
    Calls AI service and ALWAYS returns:
        {
          "move": Move,
          "metadata": dict
        }
    """

    payload = {
        "board": board,
        "current_player": current_player,
        "legal_moves": [{"row": m.row, "col": m.col} for m in legal_moves],
        "difficulty": difficulty,
    }

    url = f"{AI_SERVICE_BASE_URL.rstrip('/')}{AI_MOVE_ENDPOINT}"

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            resp = await client.post(url, json=payload)
            resp.raise_for_status()
            data = resp.json()

    except Exception as e:
        print("\n AI REQUEST FAILED:", e)
        print("URL:", url)
        print("Payload:", payload)
        print("FALLBACK MOVE\n")

        fallback_move = legal_moves[0]
        return {
            "move": fallback_move,
            "metadata": {
                "mctsIterations": 0,
                "explorationConstant": 0,
                "visitCountsJson": "{}",
                "actionScoresJson": "{}",
                "policyVectorJson": "{}",
                "bestVisitCount": 0,
                "bestQValue": 0,
                "moveConfidence": 0,
                "branchingFactor": len(legal_moves),
                "exploredActions": 0,
                "mctsTreeSize": 0,
                "rolloutCount": 0,
                "rolloutDepthAverage": 0,
                "rolloutDepthMax": 0,
                "mctsSearchDurationMs": 0,
                "aiLatencyMs": 0,
                "aiDifficulty": difficulty.upper(),
            }
        }

    print("\nAI RAW RESPONSE:", data)

    # Build Move object
    move = Move(row=int(data["row"]), col=int(data["col"]))

    # Metadata is the entire dictionary minus row/col
    metadata = {k: v for k, v in data.items() if k not in ("row", "col")}

    return {
        "move": move,
        "metadata": metadata
    }
