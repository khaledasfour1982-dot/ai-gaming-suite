from datetime import datetime, timezone
from typing import Tuple

from game_engine.infrastructure.messaging.rabbitmq import RabbitMQPublisher


class AchievementEvaluator:

    @staticmethod
    def _center_cell(board) -> Tuple[int, int]:
        """Return the logical center cell for any NxN board."""
        n = len(board)
        return (n // 2, n // 2)

    @staticmethod
    def _is_center(move, board) -> bool:
        return (move.row, move.col) == AchievementEvaluator._center_cell(board)

    @staticmethod
    def _corner_cells(board) -> set[Tuple[int, int]]:
        """Return dynamically calculated corners for any NxN board."""
        n = len(board)
        return {
            (0, 0),
            (0, n - 1),
            (n - 1, 0),
            (n - 1, n - 1)
        }

    @staticmethod
    def _is_corner(move, board) -> bool:
        return (move.row, move.col) in AchievementEvaluator._corner_cells(board)

    @staticmethod
    def _count_player_marks(board, player_mark: str) -> int:
        return sum(cell == player_mark for row in board for cell in row)

    @staticmethod
    def _board_is_full(board, empty_mark: str) -> bool:
        return all(cell != empty_mark for row in board for cell in row)

    @staticmethod
    async def _publish(session, player_id, achievement_type, description):
        event = {
            "gameSessionId": session.session_id,
            "playerId": player_id,
            "name": achievement_type,
            "description": description,
            "timestamp": datetime.now(timezone.utc).isoformat()
        }

        await RabbitMQPublisher.publish("tictactoe.achievement.acquired", event)
        print(f"🏆 Achievement earned: {achievement_type} by {player_id}")

    # --------------------------------------------------
    # ACHIEVEMENT RULES
    # --------------------------------------------------

    @staticmethod
    async def check_center_control(session, state_before, move, player_id):
        """Place your symbol in the center."""
        if AchievementEvaluator._is_center(move, state_before.board):
            await AchievementEvaluator._publish(
                session,
                player_id,
                "CENTER_CONTROL",
                "Place your symbol in the center square"
            )

    @staticmethod
    async def check_corner_strategist(session, state_before, move, player_id, outcome):
        """
        Win by *starting* in any corner.
        Condition:
        - This must be the player's FIRST move.
        - The first move must be a corner cell.
        """
        if outcome.result.value!="WIN":
            return

        player_mark = state_before.current_player
        marks_before = AchievementEvaluator._count_player_marks(state_before.board, player_mark)

        if marks_before != 0:
            return  # not the player's first move

        if AchievementEvaluator._is_corner(move, state_before.board):
            await AchievementEvaluator._publish(
                session,
                player_id,
                "CORNER_STRATEGIST",
                "Player wins by starting in any corner"
            )

    @staticmethod
    async def check_quick_win(session, state_after, config, player_id, outcome):
        """Win in fewer than 5 moves (global, not per-player)."""
        if state_after.config.marks is None:
            return

        if outcome.result.value!="WIN":
            return

        empty = config.empty_mark

        total_moves = sum(cell != empty for row in state_after.board for cell in row)

        if total_moves <= 5:
            await AchievementEvaluator._publish(
                session,
                player_id,
                "QUICK_WIN",
                "Win a game in under 5 moves"
            )

    @staticmethod
    async def check_last_second_win(session, state_after, config, player_id, outcome):
        """Win on the final move of the game."""
        if outcome.result.value!="WIN":
            return

        if AchievementEvaluator._board_is_full(state_after.board, config.empty_mark):
            await AchievementEvaluator._publish(
                session,
                player_id,
                "LAST_SECOND_WIN",
                "Win on the final board cell"
            )

    # --------------------------------------------------
    # PUBLIC DISPATCHER
    # --------------------------------------------------

    @staticmethod
    async def evaluate_move_achievements(session, state_before, move, player_id):
        await AchievementEvaluator.check_center_control(session, state_before, move, player_id)
        #await AchievementEvaluator.check_corner_strategist(session, state_before, move, player_id)

    @staticmethod
    async def evaluate_endgame_achievements(session, final_state, player_id, outcome):
        if session.game_status != "FINISHED":
            return

        await AchievementEvaluator.check_quick_win(session, final_state, final_state.config, player_id, outcome)
        await AchievementEvaluator.check_last_second_win(session, final_state, final_state.config, player_id, outcome)
