CREATE SCHEMA IF NOT EXISTS logging;

CREATE TABLE logging.ai_simulation_logs (
                                         log_id UUID PRIMARY KEY,
                                         game_session_id UUID NOT NULL,
                                         player_id UUID NOT NULL,
                                         played_by VARCHAR(10) NOT NULL,

                                         board_state JSONB NOT NULL,
                                         legal_moves JSONB NOT NULL,
                                         chosen_move JSONB NOT NULL,

                                         game_status VARCHAR(20) NOT NULL,
                                         logged_at TIMESTAMP NOT NULL,

                                         ai_difficulty VARCHAR(20),
                                         outcome VARCHAR(20),

                                         mcts_iterations INT,
                                         exploration_constant DOUBLE PRECISION,

                                         visit_counts JSONB,
                                         action_scores JSONB,
                                         policy_vector JSONB,

                                         best_visit_count INT,
                                         best_q_value DOUBLE PRECISION,
                                         move_confidence DOUBLE PRECISION,
                                         branching_factor INT,
                                         explored_actions INT,
                                         mcts_tree_size INT,

                                         rollout_count INT,
                                         rollout_depth_average DOUBLE PRECISION,
                                         rollout_depth_max INT,

                                         mcts_search_duration_ms BIGINT,
                                         ai_latency_ms BIGINT,

    -- ---- GRANDMASTER SUGGESTION DATA ----
                                         gm_suggested_move JSONB,
                                         move_matches_gm BOOLEAN,

                                         gm_mcts_iterations INT,
                                         gm_exploration_constant DOUBLE PRECISION,

                                         gm_visit_counts JSONB,
                                         gm_action_scores JSONB,
                                         gm_policy_vector JSONB,

                                         gm_best_visit_count INT,
                                         gm_best_q_value DOUBLE PRECISION,
                                         gm_move_confidence DOUBLE PRECISION,

                                         gm_explored_actions INT,
                                         gm_mcts_tree_size INT,
                                         gm_mcts_search_duration_ms BIGINT
);
