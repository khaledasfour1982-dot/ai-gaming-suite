# game_engine/infrastructure/platform_player_repository.py
"""
Repository for querying player statistics from platform_postgres.
"""

from typing import Optional
from uuid import UUID
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from game_engine.infrastructure.platform_database import platform_db


class PlayerStats:
    """Data class for player statistics."""
    
    def __init__(self, wins: int, losses: int, draws: int, games_played: int):
        self.wins = wins
        self.losses = losses
        self.draws = draws
        self.games_played = games_played
    
    @property
    def win_rate(self) -> float:
        """Calculate win rate as wins / games_played."""
        if self.games_played == 0:
            return 0.0
        return self.wins / self.games_played


class PlatformPlayerRepository:
    """Repository for querying player_stats from platform database."""

    @staticmethod
    async def get_player_stats(
        player_id: str, 
        match_type: str = "HUMAN_VS_AI"
    ) -> Optional[PlayerStats]:
        """
        Get player statistics from platform_postgres.
        
        This method gracefully handles failures - if platform database is unavailable,
        it returns None (which will cause adaptive difficulty to default to "medium").
        
        Args:
            player_id: UUID string of the player
            match_type: Match type filter (default: "HUMAN_VS_AI")
        
        Returns:
            PlayerStats object or None if player not found or database unavailable
        """
        # Check if platform database is connected
        if not platform_db.is_connected:
            return None
        
        try:
            # Convert player_id to UUID for query
            player_uuid = UUID(player_id)
        except ValueError:
            # Invalid UUID format
            return None

        try:
            async with platform_db.session() as session:
                # Query player_stats table in kdg_platform schema
                query = text("""
                    SELECT wins, losses, draws, games_played
                    FROM kdg_platform.player_stats
                    WHERE player_id = :player_id
                      AND match_type = :match_type
                    LIMIT 1
                """)
                
                result = await session.execute(
                    query,
                    {"player_id": str(player_uuid), "match_type": match_type}
                )
                
                row = result.fetchone()
                
                if row is None:
                    return None
                
                wins, losses, draws, games_played = row
                
                return PlayerStats(
                    wins=wins or 0,
                    losses=losses or 0,
                    draws=draws or 0,
                    games_played=games_played or 0
                )
        except Exception as e:
            # Gracefully handle any database errors (connection issues, etc.)
            # Log but don't fail - game engine should continue working
            print(f"⚠ Failed to fetch player stats from platform (using default difficulty): {e}")
            return None

