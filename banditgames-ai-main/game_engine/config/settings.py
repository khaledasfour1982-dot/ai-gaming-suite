import os
from dataclasses import dataclass
from game_engine.domain.models import GameConfig, SelectedMarks, MarkType


@dataclass(frozen=True)
class GameEngineSettings:
    size: int = 3
    empty_mark: str = ""

    # backend decides which marks are used
    marks: SelectedMarks = SelectedMarks(MarkType.X, MarkType.O)

    @property
    def game_config(self) -> GameConfig:
        return GameConfig(
            size=self.size,
            marks=self.marks
        )

    @property
    def platform_database_url(self) -> str:
        """Platform database URL for accessing player_stats."""
        return os.getenv(
            "PLATFORM_DATABASE_URL",
            "postgresql+asyncpg://user:password@platform_postgres:5432/postgres"
        )


SETTINGS = GameEngineSettings()
