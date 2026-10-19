# game_engine/service/ai_vs_ai_service.py

from uuid import uuid4
from game_engine.domain.rules import compute_legal_moves, play_move
from game_engine.domain.models import GameState
from game_engine.config.settings import SETTINGS
from game_engine.aiCommunication.AgentEngine import call_external_ai

from game_engine.infrastructure.log_repository import save_ai_simulation_log


class AiVsAiService:

    @staticmethod
    async def simulate(iterations: int, x_difficulty: str, o_difficulty: str):
        """
        Runs AI vs AI simulation for N games.
        Returns total X wins, O wins, draws.

        For each move, also gets a Grandmaster suggestion (without playing it)
        to compare the chosen move with optimal play.
        """

        x_wins = 0
        o_wins = 0
        draws = 0

        config = SETTINGS.game_config

        for _ in range(iterations):

            # ------------------------------
            # Start with empty board
            # ------------------------------
            board = [
                [config.empty_mark for _ in range(config.size)]
                for _ in range(config.size)
            ]

            state = GameState(
                board=board,
                current_player=config.marks.p1.value,  # X always starts
                config=config
            )

            simulation_session_id = str(uuid4())
            game_status = "IN_PROGRESS"

            # ------------------------------
            # Main game loop
            # ------------------------------
            while game_status == "IN_PROGRESS":
                legal_moves = compute_legal_moves(state)

                # Select correct difficulty for the current player
                difficulty = (
                    x_difficulty if state.current_player == config.marks.p1.value
                    else o_difficulty
                )

                # ------------------------------
                # CALL EXTERNAL AI (actual move)
                # returns dictionary + Move object
                # ------------------------------
                ai_result = await call_external_ai(
                    board=state.board,
                    current_player=state.current_player,
                    legal_moves=legal_moves,
                    difficulty=difficulty,
                )

                move = ai_result["move"]            # Move object
                metadata = ai_result["metadata"]    # dict containing all MCTS fields

                # ------------------------------
                # GET GRANDMASTER SUGGESTION (for comparison)
                # This doesn't affect gameplay - just for analysis
                # ------------------------------
                gm_result = await call_external_ai(
                    board=state.board,
                    current_player=state.current_player,
                    legal_moves=legal_moves,
                    difficulty="grandmaster",
                )

                gm_move = gm_result["move"]            # Move object
                gm_metadata = gm_result["metadata"]    # dict containing all GM MCTS fields

                # Check if chosen move matches GM suggestion
                move_matches_gm = (move.row == gm_move.row and move.col == gm_move.col)

                # ------------------------------
                # Apply move using Python engine
                # ------------------------------
                new_state, status, outcome, _ = play_move(state, move)

                # ------------------------------
                # Save LOG ENTRY with both actual and GM data
                # ------------------------------
                await save_ai_simulation_log(
                    session_id=simulation_session_id,
                    player_mark=state.current_player,
                    board=state.board,
                    legal_moves=[{"row": m.row, "col": m.col} for m in legal_moves],
                    move=move,
                    metadata=metadata,
                    gm_move=gm_move,
                    gm_metadata=gm_metadata,
                    move_matches_gm=move_matches_gm,
                    game_status=status.value,
                    final_result=(
                        outcome.result.value
                        if status.value == "FINISHED"
                        else None
                    )
                )

                # Update game state
                state = new_state
                game_status = status.value

                # End-of-game stats
                if game_status == "FINISHED":
                    if outcome.result.value == "WIN":
                        if outcome.winner == config.marks.p1.value:
                            x_wins += 1
                        else:
                            o_wins += 1
                    else:
                        draws += 1

        return {
            "iterations": iterations,
            "x_wins": x_wins,
            "o_wins": o_wins,
            "draws": draws
        }