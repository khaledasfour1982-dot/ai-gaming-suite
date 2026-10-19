# def test_api_game_move_basic(client):
#     response = client.post(
#         "/game/move",
#         json={
#             "board": [["", "", ""], ["", "", ""], ["", "", ""]],
#             "current_player": "X",
#             "move": {"row": 0, "col": 0}
#         }
#     )
#
#     assert response.status_code == 200
#     data = response.json()
#     assert data["board"][0][0] == "X"
#     assert data["current_player"] == "O"
#     assert data["game_status"] == "IN_PROGRESS"
