from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import joblib
import numpy as np

import sys
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from policy_preprocessor_pipeline import PolicyPreprocessor

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

MODEL_PATH = os.path.join(BASE_DIR, "policy_model.pkl")
PREPROCESSOR_PATH = os.path.join(BASE_DIR, "policy_preprocessor.pkl")

# Load model and preprocessor
model = joblib.load(MODEL_PATH)
preprocessor = joblib.load(PREPROCESSOR_PATH)

router = APIRouter(
    prefix="/ml",
    tags=["ML Policy Model"]
)

# ---------------------------
# Request schema
# ---------------------------
class BoardRequest(BaseModel):
    board: list        # 3x3 matrix [["X","O",""], ...]
    player_mark: str   # "X" or "O"

# ---------------------------
# Predict Move Endpoint
# ---------------------------
@router.post("/predict_move")
def predict_move(req: BoardRequest):

    board = req.board
    player_mark = req.player_mark  # VERY IMPORTANT

    # Encode full feature vector (board + legal mask)
    encoded = preprocessor.encode_inference_input(board, player_mark).reshape(1, -1)

    # Predict using MLPRegressor (returns raw scores, not probabilities)
    # Shape: (1, 9) - one score per board position
    raw_scores = model.predict(encoded)[0]  # Get the first (and only) prediction

    # Build legal mask to filter out illegal moves
    legal_mask = preprocessor.encode_legal_mask(board).astype(float)

    # Zero out illegal moves
    filtered_scores = raw_scores * legal_mask

    # Check if any legal moves remain
    if filtered_scores.sum() == 0 or np.all(legal_mask == 0):
        raise HTTPException(status_code=400, detail="No legal moves available")

    # Select move with highest score
    class_id = int(np.argmax(filtered_scores))
    row, col = preprocessor.decode_move(class_id)

    # Optional: Convert to probabilities using softmax for better interpretation
    # Only compute softmax over legal moves
    legal_indices = np.where(legal_mask > 0)[0]
    legal_scores = filtered_scores[legal_indices]

    # Softmax to get probabilities
    exp_scores = np.exp(legal_scores - np.max(legal_scores))  # Subtract max for numerical stability
    probabilities = exp_scores / exp_scores.sum()

    # Get confidence for the selected move
    selected_idx = np.where(legal_indices == class_id)[0][0]
    confidence = float(probabilities[selected_idx])

    return {
        "row": row,
        "col": col,
        "class_id": class_id,
        "confidence": confidence,
        "raw_score": float(raw_scores[class_id])
    }

# ---------------------------
# Health Check Endpoint
# ---------------------------
@router.get("/health")
def health_check():
    return {
        "status": "ok",
        "model_loaded": model is not None,
        "preprocessor_loaded": preprocessor is not None,
        "model_type": type(model).__name__,
        "uses_metadata": preprocessor.use_gm_metadata if hasattr(preprocessor, 'use_gm_metadata') else "unknown"
    }


# ---------------------------
# Get All Predictions Endpoint (for debugging)
# ---------------------------
@router.post("/predict_all")
def predict_all(req: BoardRequest):
    """Returns scores for all positions (useful for debugging)"""
    try:
        board = req.board
        player_mark = req.player_mark

        # Encode input
        encoded = preprocessor.encode_inference_input(board, player_mark).reshape(1, -1)

        # Predict
        raw_scores = model.predict(encoded)[0]

        # Get legal mask
        legal_mask = preprocessor.encode_legal_mask(board)

        # Build response with all positions
        positions = []
        for i in range(9):
            row, col = preprocessor.decode_move(i)
            positions.append({
                "position": i,
                "row": row,
                "col": col,
                "raw_score": float(raw_scores[i]),
                "is_legal": bool(legal_mask[i])
            })

        # Sort by score (descending)
        positions.sort(key=lambda x: x["raw_score"], reverse=True)

        return {
            "positions": positions,
            "best_legal_move": next(
                (p for p in positions if p["is_legal"]),
                None
            )
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction error: {str(e)}")