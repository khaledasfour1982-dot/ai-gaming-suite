# ai/service.py
import time
from typing import Dict, Any

from ai.mcts import mcts                     # <-- our custom MCTS factory
from ai.mcts_wrapper import TicTacToeStateWrapper

from game_engine.domain.models import GameState
from game_engine.domain.move import Move
from game_engine.domain.rules import compute_legal_moves

from ai.config import (
    EASY_ITERATIONS,
    MEDIUM_ITERATIONS,
    HARD_ITERATIONS,
    GRANDMASTER_ITERATIONS
)


# ============================================================
# Utility: iteration count by difficulty
# ============================================================
def _iteration_limit(difficulty: str) -> int:
    d = (difficulty or "medium").lower()
    if d == "easy":
        return EASY_ITERATIONS
    if d == "hard":
        return HARD_ITERATIONS
    if d == "grandmaster":
        return GRANDMASTER_ITERATIONS
    return MEDIUM_ITERATIONS


# ============================================================
# MAIN AI ENTRYPOINT
# ============================================================
def choose_move_mcts(state: GameState, difficulty: str = "medium") -> Dict[str, Any]:
    """
    Builds the wrapper, runs MCTS, returns AIMoveDto metadata.
    """

    # --- normalize board (important for EMPTY markers) ---
    def normalize(board, empty):
        return [
            [empty if cell in ("", ".", "EMPTY", None) else cell for cell in row]
            for row in board
        ]

    clean_board = normalize(state.board, state.config.empty_mark)

    # rebuild GameState with cleaned board
    state = GameState(
        board=clean_board,
        current_player=state.current_player,
        config=state.config,
    )

    legal_moves = compute_legal_moves(state)

    # --- wrap for MCTS ---
    wrapper = TicTacToeStateWrapper.from_game_state(state)

    # difficulty parameters
    iteration_limit = _iteration_limit(difficulty)
    exploration_constant = 1 / (2 ** 0.5)

    print("\n=== AI MOVE REQUEST ===")
    print(f"Turn player: {state.current_player}")
    print(f"Difficulty: {difficulty.upper()}")
    print(f"Iterations: {iteration_limit}")

    # --- create MCTS solver ---
    solver = mcts(
        iterationLimit=iteration_limit,
        explorationConstant=exploration_constant,
        difficulty=difficulty.lower(),
    )

    # --- run MCTS ---
    t0 = time.time()
    chosen_move = solver.search(initialState=wrapper)
    t1 = time.time()
    latency_ms = int((t1 - t0) * 1000)

    if not isinstance(chosen_move, Move):
        raise RuntimeError("MCTS returned non-Move object")

    # ============================================================
    # METADATA COLLECTION (Java AIMoveDto format)
    # ============================================================

    visit_counts = {}
    action_scores = {}
    total_visits = 0
    explored_actions = 0

    for move, child in solver.root.children.items():
        key = f"{move.row},{move.col}"
        visit_counts[key] = child.numVisits
        total_visits += child.numVisits
        explored_actions += 1

        if child.numVisits > 0:
            action_scores[key] = child.totalReward / child.numVisits
        else:
            action_scores[key] = 0.0

    # Normalized visit distribution
    policy_vector = {
        k: (v / total_visits) if total_visits > 0 else 0.0
        for k, v in visit_counts.items()
    }

    best_visit_count = max(visit_counts.values()) if visit_counts else 0
    best_q_value = max(action_scores.values()) if action_scores else 0.0

    move_key = f"{chosen_move.row},{chosen_move.col}"
    move_confidence = policy_vector.get(move_key, 0.0)

    # Count total nodes in MCTS tree (for debugging / stats)
    def count_nodes(node):
        return 1 + sum(count_nodes(child) for child in node.children.values())

    mcts_tree_size = count_nodes(solver.root)

    # ============================================================
    # RETURN AIMoveDto
    # ============================================================
    return {
        "row": chosen_move.row,
        "col": chosen_move.col,

        "mctsIterations": iteration_limit,
        "explorationConstant": exploration_constant,

        "visitCountsJson": str(visit_counts),
        "actionScoresJson": str(action_scores),
        "policyVectorJson": str(policy_vector),

        "bestVisitCount": best_visit_count,
        "bestQValue": best_q_value,
        "moveConfidence": move_confidence,

        "branchingFactor": len(legal_moves),
        "exploredActions": explored_actions,
        "mctsTreeSize": mcts_tree_size,

        # no rollout stats (because smart rollouts do not use random depth)
        "rolloutCount": iteration_limit,
        "rolloutDepthAverage": 0,
        "rolloutDepthMax": 0,

        "mctsSearchDurationMs": latency_ms,
        "aiLatencyMs": latency_ms,
        "aiDifficulty": difficulty.upper(),
    }