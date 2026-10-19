from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from typing import List, Literal, Optional

import joblib
import pandas as pd
import numpy as np

MODEL_PATH = "winprob_best_model.joblib"
MODEL_VERSION = "winprob_v1"

model = joblib.load(MODEL_PATH)

app = FastAPI(title="Win Probability Service")

# ---- request schema ----
class WinProbRequest(BaseModel):
    game_session_id: str
    board: List[List[str]]
    player_mark: Literal["X", "O"]
    move_index: int
    ai_difficulty: Optional[Literal["EASY", "MEDIUM", "HARD"]] = "MEDIUM"

# ---- helper: board -> vec + counts ----
def encode_board(board_matrix: List[List[str]]) -> np.ndarray:
    """
    ""/None -> 0, "X" -> 1, "O" -> 2
    """
    value_map = {
        None: 0,
        "": 0,
        " ": 0,
        "X": 1,
        "CROSS": 1,
        "PLAYER_X": 1,
        "O": 2,
        "CIRCLE": 2,
        "PLAYER_O": 2,
    }

    n_rows = len(board_matrix)
    n_cols = len(board_matrix[0])

    vec = []
    for r in range(n_rows):
        row = board_matrix[r]
        if len(row) < n_cols:
            row = row + [None] * (n_cols - len(row))
        for c in range(n_cols):
            vec.append(value_map.get(row[c], 0))

    return np.array(vec, dtype=int)

def build_feature_row(req: WinProbRequest) -> pd.DataFrame:
    board_vec = encode_board(req.board)

    board_X_count = int((board_vec == 1).sum())
    board_O_count = int((board_vec == 2).sum())
    board_empty_count = int((board_vec == 0).sum())

    played_by_encoded = 0 if req.player_mark == "X" else 1

    row = {
        "move_index": req.move_index,
        "played_by_encoded": played_by_encoded,
        "board_X_count": board_X_count,
        "board_O_count": board_O_count,
        "board_empty_count": board_empty_count,
        "ai_difficulty": req.ai_difficulty,
    }

    # board_0..board_8
    for i, v in enumerate(board_vec):
        row[f"board_{i}"] = int(v)

    X = pd.DataFrame([row])
    return X

# ---- endpoints ----
@app.get("/model")
def get_model():
    return {"status": "ok", "model_version": MODEL_VERSION}

@app.post("/predict_win_probability")
def predict_win_probability(req: WinProbRequest):
    X = build_feature_row(req)
    prob = float(model.predict(X)[0])

    return {
        "win_probability": prob,
        "model_version": MODEL_VERSION,
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "game_session_id": req.game_session_id,
    }
