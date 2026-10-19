from fastapi import APIRouter, Response
import pandas as pd
from io import BytesIO

from sqlalchemy import select
from game_engine.infrastructure.database import db
from game_engine.infrastructure.ai_log_models import AILogEntry

router = APIRouter(prefix="/api/ai", tags=["AI Export"])


@router.get("/export", response_description="Download AI logs as parquet")
async def export_ai_logs():
    """
    Reads all rows from ai_simulation_logs table and
    returns a downloadable parquet file.
    """

    # --- Load rows from DB ---
    async with db.session() as session:
        stmt = select(AILogEntry)
        result = await session.execute(stmt)
        rows = result.scalars().all()

    # Convert ORM objects → dict rows for pandas
    records = []
    for r in rows:
        records.append({
            # ---- CORE ----
            "id": str(r.id),
            "session_id": r.session_id,
            "player_mark": r.player_mark,

            "board": r.board,
            "legal_moves": r.legal_moves,
            "ai_move": r.ai_move,

            "game_status": r.game_status,
            "timestamp": r.timestamp,

            # ---- AI MCTS METADATA ----
            "mcts_iterations": r.mcts_iterations,
            "exploration_constant": r.exploration_constant,

            "visit_counts_json": r.visit_counts_json,
            "action_scores_json": r.action_scores_json,
            "policy_vector_json": r.policy_vector_json,

            "best_visit_count": r.best_visit_count,
            "best_q_value": r.best_q_value,
            "move_confidence": r.move_confidence,

            "branching_factor": r.branching_factor,
            "explored_actions": r.explored_actions,
            "mcts_tree_size": r.mcts_tree_size,

            "rollout_count": r.rollout_count,
            "rollout_depth_avg": r.rollout_depth_avg,
            "rollout_depth_max": r.rollout_depth_max,

            "mcts_search_duration_ms": r.mcts_search_duration_ms,
            "ai_latency_ms": r.ai_latency_ms,

            "ai_difficulty": r.ai_difficulty,

            # ---- GRANDMASTER SUGGESTION DATA ----
            "gm_suggested_move": r.gm_suggested_move,
            "move_matches_gm": r.move_matches_gm,

            "gm_mcts_iterations": r.gm_mcts_iterations,
            "gm_exploration_constant": r.gm_exploration_constant,

            "gm_visit_counts_json": r.gm_visit_counts_json,
            "gm_action_scores_json": r.gm_action_scores_json,
            "gm_policy_vector_json": r.gm_policy_vector_json,

            "gm_best_visit_count": r.gm_best_visit_count,
            "gm_best_q_value": r.gm_best_q_value,
            "gm_move_confidence": r.gm_move_confidence,

            "gm_explored_actions": r.gm_explored_actions,
            "gm_mcts_tree_size": r.gm_mcts_tree_size,

            "gm_mcts_search_duration_ms": r.gm_mcts_search_duration_ms,

            # ---- GAME RESULT ----
            "final_result": r.final_result,
        })


    # Build dataframe
    df = pd.DataFrame(records)

    # Write to in-memory parquet buffer
    buffer = BytesIO()
    df.to_parquet(buffer, index=False, engine="pyarrow")
    buffer.seek(0)

    # Return parquet file as download
    return Response(
        content=buffer.read(),
        media_type="application/octet-stream",
        headers={
            "Content-Disposition": "attachment; filename=ai_logs.parquet"
        }
    )
