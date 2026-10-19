# from unittest.mock import AsyncMock
# from game_engine.api.dto.MoveDTO import MoveDTO
#
#
# def test_human_move_then_ai_move(client, mocker):
#     """
#     Simulates a turn sequence:
#     1. Human (X) makes a move.
#     2. Next player is AI (O), so the same request triggers AI move.
#     """
#
#     # -----------------------
#     # STEP 1: Human moves     (X -> plays (0,0))
#     # -----------------------
#
#     # Mock AI POST call for next step
#     mock_post = AsyncMock()
#     # AI will choose row=2,col=2 in this test
#     mock_post.json.return_value = {"row": 2, "col": 2}
#
#     mocker.patch(
#         "game_engine.api.controllers.game_controller.httpx.AsyncClient.post",
#         return_value=mock_post,
#     )
#
#     payload = {
#         "board": [
#             ["", "", ""],
#             ["", "", ""],
#             ["", "", ""]
#         ],
#         "current_player": "X",
#         "move": {"row": 0, "col": 0},  # human move
#         "ai_player": "O",
#         "difficulty": "medium"
#     }
#
#     res = client.post("/game/move", json=payload)
#     assert res.status_code == 200
#     data = res.json()
#
#     # -----------------------
#     # Assertions for HUMAN move
#     # -----------------------
#     assert data["board"][0][0] == "X"           # human move applied
#     assert data["move"] == {"row": 0, "col": 0}
#     assert data["current_player"] == "O"         # turn switches to AI
#     assert data["game_status"] == "IN_PROGRESS"
#
#     # -----------------------
#     # STEP 2 triggered: AI should have moved automatically
#     # -----------------------
#
#     # AI move should now be placed on board
#     assert data["board"][2][2] == "O"
#
#     # Move returned should be the AI move (not the human one)
#     assert data["move"] == {"row": 2, "col": 2}  # The last applied move
#
#     # After AI, next player is human again (X)
#     assert data["current_player"] == "X"
