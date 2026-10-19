import json
from datetime import datetime
from game_engine.infrastructure.database import db
from game_engine.infrastructure.ai_log_models import AILogEntry


async def save_ai_simulation_log(
        session_id: str,
        player_mark: str,
        board: list[list[str]],
        legal_moves: list[dict],
        move,
        metadata: dict,
        gm_move,
        gm_metadata: dict,
        move_matches_gm: bool,
        game_status: str,
        final_result: str | None
):
    """
    Persists a complete AI simulation log entry into PostgreSQL.
    Mirrors the Java MoveMadeEvent metadata structure.
    
    Now includes Grandmaster suggestion data for comparison:
    - gm_suggested_move: What the Grandmaster AI would have played
    - gm_* fields: All MCTS metrics from the Grandmaster analysis
    - move_matches_gm: Whether the actual move matched GM suggestion
    """

    async with db.session() as s:
        entry = AILogEntry(
            session_id=session_id,
            player_mark=player_mark,

            board=json.dumps(board),
            legal_moves=json.dumps(legal_moves),

            ai_move=json.dumps({
                "row": move.row,
                "col": move.col
            }),

            game_status=game_status,
            timestamp=datetime.utcnow(),

            # ---- ACTUAL MCTS metadata (from chosen difficulty) ----
            mcts_iterations=metadata["mctsIterations"],
            exploration_constant=str(metadata["explorationConstant"]),

            visit_counts_json=str(metadata["visitCountsJson"]),
            action_scores_json=str(metadata["actionScoresJson"]),
            policy_vector_json=str(metadata["policyVectorJson"]),

            best_visit_count=metadata["bestVisitCount"],
            best_q_value=str(metadata["bestQValue"]),
            move_confidence=str(metadata["moveConfidence"]),

            branching_factor=metadata["branchingFactor"],
            explored_actions=metadata["exploredActions"],
            mcts_tree_size=metadata["mctsTreeSize"],

            rollout_count=metadata["rolloutCount"],
            rollout_depth_avg=str(metadata["rolloutDepthAverage"]),
            rollout_depth_max=metadata["rolloutDepthMax"],

            mcts_search_duration_ms=metadata["mctsSearchDurationMs"],
            ai_latency_ms=metadata["aiLatencyMs"],

            ai_difficulty=metadata["aiDifficulty"],

            # ---- GRANDMASTER SUGGESTION DATA ----
            gm_suggested_move=json.dumps({
                "row": gm_move.row,
                "col": gm_move.col
            }),

            move_matches_gm=move_matches_gm,

            gm_mcts_iterations=gm_metadata["mctsIterations"],
            gm_exploration_constant=str(gm_metadata["explorationConstant"]),

            gm_visit_counts_json=str(gm_metadata["visitCountsJson"]),
            gm_action_scores_json=str(gm_metadata["actionScoresJson"]),
            gm_policy_vector_json=str(gm_metadata["policyVectorJson"]),

            gm_best_visit_count=gm_metadata["bestVisitCount"],
            gm_best_q_value=str(gm_metadata["bestQValue"]),
            gm_move_confidence=str(gm_metadata["moveConfidence"]),

            gm_explored_actions=gm_metadata["exploredActions"],
            gm_mcts_tree_size=gm_metadata["mctsTreeSize"],

            gm_mcts_search_duration_ms=gm_metadata["mctsSearchDurationMs"],

            final_result=final_result
        )

        s.add(entry)
        # No return — logging is a write-only command.