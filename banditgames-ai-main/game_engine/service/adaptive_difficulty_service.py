# game_engine/service/adaptive_difficulty_service.py
"""
Service for calculating adaptive AI difficulty based on player win rate.
"""

from game_engine.infrastructure.platform_player_repository import (
    PlatformPlayerRepository,
    PlayerStats
)


class AdaptiveDifficultyService:
    """Service for calculating adaptive AI difficulty."""

    MIN_GAMES_FOR_ADAPTATION = 5
    EASY_THRESHOLD = 0.30  # < 30% win rate
    HARD_THRESHOLD = 0.60  # > 60% win rate

    @staticmethod
    async def calculate_adaptive_difficulty(player_id: str) -> str:
        """
        Calculate adaptive AI difficulty based on player's win rate.
        
        This method gracefully handles failures - if platform database is unavailable,
        it defaults to "medium" difficulty so the game can continue.
        
        Rules:
        - < 30% win rate → "easy"
        - 30-60% win rate → "medium"
        - > 60% win rate → "hard"
        - < 5 games played → "medium" (default)
        - Platform unavailable → "medium" (default)
        
        Args:
            player_id: UUID string of the player
        
        Returns:
            Difficulty string: "easy", "medium", or "hard" (always returns a valid difficulty)
        """
        try:
            # Get player statistics
            stats = await PlatformPlayerRepository.get_player_stats(
                player_id=player_id,
                match_type="HUMAN_VS_AI"
            )
            
            # If player not found, insufficient games, or platform unavailable, return default
            if stats is None or stats.games_played < AdaptiveDifficultyService.MIN_GAMES_FOR_ADAPTATION:
                return "medium"
            
            win_rate = stats.win_rate
            
            # Apply thresholds
            if win_rate < AdaptiveDifficultyService.EASY_THRESHOLD:
                return "easy"
            elif win_rate <= AdaptiveDifficultyService.HARD_THRESHOLD:
                return "medium"
            else:
                return "hard"
        except Exception as e:
            # Gracefully handle any errors - always return a valid difficulty
            print(f"⚠ Error calculating adaptive difficulty (using default 'medium'): {e}")
            return "medium"

