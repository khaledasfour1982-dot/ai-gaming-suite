from datetime import datetime
from uuid import UUID, uuid4

from game_engine.infrastructure.database import Base, db
from sqlalchemy import String, Integer, DateTime, JSON, ForeignKey, UniqueConstraint
from sqlalchemy.ext.asyncio import (
    create_async_engine,
    async_sessionmaker,
    AsyncSession,
    AsyncEngine
)
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import UUID as PG_UUID
from sqlalchemy.sql.schema import PrimaryKeyConstraint

class GameSession(Base):
    """Game session table."""
    __tablename__ = "game_sessions"
    __table_args__ = {'schema': 'kdg_tic_tac_toe'}


    id: Mapped[UUID] = mapped_column(PG_UUID(as_uuid=True), primary_key=True, default=uuid4)
    status: Mapped[str] = mapped_column(String(20), nullable=False)
    board_state: Mapped[dict] = mapped_column(JSON, nullable=False)
    current_player: Mapped[str] = mapped_column(String(10), nullable=False)

    # Game configuration
    board_size: Mapped[int] = mapped_column(Integer, nullable=False, default=3)
    player1_mark: Mapped[str] = mapped_column(String(10), nullable=False)
    player2_mark: Mapped[str] = mapped_column(String(10), nullable=False)
    win_probability: Mapped[float | None] = mapped_column(nullable=True)

    players = relationship("SessionPlayer", back_populates="game_session")
    outcome = relationship("GameOutcome", uselist=False, back_populates="game_session")



class SessionPlayer(Base):
    __tablename__ = "session_players"
    __table_args__ = (
        PrimaryKeyConstraint("game_session_id", "id"),
        {"schema": "kdg_tic_tac_toe"}
    )

    id: Mapped[str] = mapped_column(String, nullable=False)

    game_session_id: Mapped[UUID] = mapped_column(
        PG_UUID(as_uuid=True),
        ForeignKey("kdg_tic_tac_toe.game_sessions.id", ondelete="CASCADE"),
        nullable=False
    )

    mark: Mapped[str] = mapped_column(String(10), nullable=False)
    player_type: Mapped[str] = mapped_column(String(10), nullable=False)
    ai_difficulty: Mapped[str | None] = mapped_column(String(10))

    # Relationship
    game_session: Mapped["GameSession"] = relationship(back_populates="players")


class GameOutcome(Base):
    """Final outcome of a game."""
    __tablename__ = "game_outcomes"
    __table_args__ = {'schema': 'kdg_tic_tac_toe'}

    id: Mapped[UUID] = mapped_column(PG_UUID(as_uuid=True), primary_key=True, default=uuid4)
    game_session_id: Mapped[UUID] = mapped_column(
        PG_UUID(as_uuid=True),
        ForeignKey("kdg_tic_tac_toe.game_sessions.id", ondelete="CASCADE"),
        nullable=False,
        unique=True
    )

    result: Mapped[str] = mapped_column(String(10), nullable=False)
    winner_mark: Mapped[str | None] = mapped_column(String(10), nullable=True)

    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, default=datetime.utcnow)

    # Relationship
    game_session: Mapped["GameSession"] = relationship(back_populates="outcome")


# ================================ewq============================================
# REPOSITORY LAYER (using SQLAlchemy ORM)
# ============================================================================

from sqlalchemy import select
from sqlalchemy.orm import selectinload, Mapped, mapped_column


class GameSessionRepository:
    """Repository for game session operations."""

    @staticmethod
    async def create_session(
            board_state: list[list[str]],
            current_player: str,
            board_size: int,
            player1_mark: str,
            player2_mark: str,
    ) -> str:
        """Create a new game session."""
        async with db.session() as session:
            game = GameSession(
                status="IN_PROGRESS",
                board_state=board_state,
                current_player=current_player,
                board_size=board_size,
                player1_mark=player1_mark,
                player2_mark=player2_mark,
            )
            session.add(game)
            await session.flush()  # Get the ID before commit
            return str(game.id)

    @staticmethod
    async def get_session(game_id: str) -> GameSession | None:
        """Fetch a game session by ID with all relationships."""
        async with db.session() as session:
            stmt = (
                select(GameSession)
                .options(
                    selectinload(GameSession.players),
                    selectinload(GameSession.outcome),
                    selectinload(GameSession.moves)
                )
                .where(GameSession.id == UUID(game_id))
            )
            result = await session.execute(stmt)
            return result.scalar_one_or_none()

    @staticmethod
    async def update_session(
            game_id: str,
            board_state: list[list[str]],
            current_player: str,
            status: str = "IN_PROGRESS"
    ):
        """Update game state after a move."""
        async with db.session() as session:
            stmt = select(GameSession).where(GameSession.id == UUID(game_id))
            result = await session.execute(stmt)
            game = result.scalar_one()

            game.board_state = board_state
            game.current_player = current_player
            game.status = status
            game.updated_at = datetime.utcnow()

    @staticmethod
    async def finish_session(game_id: str):
        """Mark a game session as finished."""
        async with db.session() as session:
            stmt = select(GameSession).where(GameSession.id == UUID(game_id))
            result = await session.execute(stmt)
            game = result.scalar_one()

            game.status = "FINISHED"
            game.finished_at = datetime.utcnow()
            game.updated_at = datetime.utcnow()


class PlayerRepository:
    """Repository for player operations."""

    @staticmethod
    async def add_player(
            game_id: str,
            mark: str,
            player_type: str,
            ai_difficulty: str | None = None
    ):
        """Add a player to a game session."""
        async with db.session() as session:
            player = SessionPlayer(
                game_session_id=UUID(game_id),
                mark=mark,
                player_type=player_type,
                ai_difficulty=ai_difficulty,
            )
            session.add(player)

    @staticmethod
    async def get_players(game_id: str) -> list[SessionPlayer]:
        """Get all players for a game session."""
        async with db.session() as session:
            stmt = (
                select(SessionPlayer)
                .where(SessionPlayer.game_session_id == UUID(game_id))
            )
            result = await session.execute(stmt)
            return list(result.scalars().all())


class OutcomeRepository:
    """Repository for game outcomes."""

    @staticmethod
    async def save_outcome(
            game_id: str,
            result: str,
            winner_mark: str | None = None,
    ):
        """Save the final outcome of a game."""
        async with db.session() as session:
            # Check if outcome already exists
            stmt = select(GameOutcome).where(GameOutcome.game_session_id == UUID(game_id))
            existing = await session.execute(stmt)
            outcome = existing.scalar_one_or_none()

            if outcome:
                # Update existing
                outcome.result = result
                outcome.winner_mark = winner_mark
            else:
                # Create new
                outcome = GameOutcome(
                    game_session_id=UUID(game_id),
                    result=result,
                    winner_mark=winner_mark,
                )
                session.add(outcome)

    @staticmethod
    async def get_outcome(game_id: str) -> GameOutcome | None:
        """Get the outcome of a finished game."""
        async with db.session() as session:
            stmt = select(GameOutcome).where(GameOutcome.game_session_id == UUID(game_id))
            result = await session.execute(stmt)
            return result.scalar_one_or_none()


