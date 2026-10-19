# def test_human_move_basic(client):
#     payload = {
#         "board": [
#             ["", "", ""],
#             ["", "", ""],
#             ["", "", ""]
#         ],
#         "current_player": "X",
#         "move": {"row": 0, "col": 0},
#         "ai_player": None
#     }
#
#     res = client.post("/game/move", json=payload)
#     assert res.status_code == 200
#
#     data = res.json()
#
#     assert data["board"][0][0] == "X"
#     assert data["current_player"] == "O"        # next player
#     assert data["game_status"] == "IN_PROGRESS"
#     assert len(data["legal_moves"]) == 8
