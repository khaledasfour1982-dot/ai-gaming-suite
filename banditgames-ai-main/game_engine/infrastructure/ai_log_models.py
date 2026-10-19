# game_engine/infrastructure/models/ai_log_models.py

from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import String, Integer, DateTime, JSON, Boolean
from sqlalchemy.dialects.postgresql import UUID as PG_UUID
from uuid import uuid4
from datetime import datetime

from game_engine.infrastructure.database import Base


class AILogEntry(Base):
    __tablename__ = "ai_simulation_logs"
    __table_args__ = {"schema": "logging"}

    # ---- PRIMARY KEY ----
    id: Mapped[str] = mapped_column(
        PG_UUID(as_uuid=True),
        primary_key=True,
        default=uuid4
    )

    # ---- CORE GAME DATA ----
    session_id: Mapped[str] = mapped_column(String, nullable=False)
    player_mark: Mapped[str] = mapped_column(String(5), nullable=False)

    board: Mapped[dict] = mapped_column(JSON)
    legal_moves: Mapped[dict] = mapped_column(JSON)
    ai_move: Mapped[dict] = mapped_column(JSON)

    game_status: Mapped[str] = mapped_column(String(20))
    timestamp: Mapped[datetime] = mapped_column(
        DateTime,
        default=datetime.utcnow
    )

    # ---- AI MCTS METADATA ----
    mcts_iterations: Mapped[int] = mapped_column(Integer)
    exploration_constant: Mapped[str] = mapped_column(String)

    visit_counts_json: Mapped[str] = mapped_column(String)
    action_scores_json: Mapped[str] = mapped_column(String)
    policy_vector_json: Mapped[str] = mapped_column(String)

    best_visit_count: Mapped[int] = mapped_column(Integer)
    best_q_value: Mapped[str] = mapped_column(String)
    move_confidence: Mapped[str] = mapped_column(String)

    branching_factor: Mapped[int] = mapped_column(Integer)
    explored_actions: Mapped[int] = mapped_column(Integer)
    mcts_tree_size: Mapped[int] = mapped_column(Integer)

    rollout_count: Mapped[int] = mapped_column(Integer)
    rollout_depth_avg: Mapped[str] = mapped_column(String)
    rollout_depth_max: Mapped[int] = mapped_column(Integer)

    mcts_search_duration_ms: Mapped[int] = mapped_column(Integer)
    ai_latency_ms: Mapped[int] = mapped_column(Integer)

    ai_difficulty: Mapped[str] = mapped_column(String(20))

    # ---- GRANDMASTER SUGGESTION DATA (NEW) ----
    gm_suggested_move: Mapped[dict | None] = mapped_column(JSON, nullable=True)
    move_matches_gm: Mapped[bool | None] = mapped_column(Boolean, nullable=True)

    gm_mcts_iterations: Mapped[int | None] = mapped_column(Integer, nullable=True)
    gm_exploration_constant: Mapped[str | None] = mapped_column(String, nullable=True)

    gm_visit_counts_json: Mapped[str | None] = mapped_column(String, nullable=True)
    gm_action_scores_json: Mapped[str | None] = mapped_column(String, nullable=True)
    gm_policy_vector_json: Mapped[str | None] = mapped_column(String, nullable=True)

    gm_best_visit_count: Mapped[int | None] = mapped_column(Integer, nullable=True)
    gm_best_q_value: Mapped[str | None] = mapped_column(String, nullable=True)
    gm_move_confidence: Mapped[str | None] = mapped_column(String, nullable=True)

    gm_explored_actions: Mapped[int | None] = mapped_column(Integer, nullable=True)
    gm_mcts_tree_size: Mapped[int | None] = mapped_column(Integer, nullable=True)

    gm_mcts_search_duration_ms: Mapped[int | None] = mapped_column(Integer, nullable=True)

    # ---- GAME RESULT ----
    final_result: Mapped[str | None] = mapped_column(String(20), nullable=True)
