# game_engine/infrastructure/platform_database.py
"""
Platform database connection for accessing player_stats from platform_postgres.
"""

from contextlib import asynccontextmanager
from typing import AsyncGenerator

from sqlalchemy import text
from sqlalchemy.ext.asyncio import (
    create_async_engine,
    async_sessionmaker,
    AsyncSession,
    AsyncEngine
)

from game_engine.config.settings import SETTINGS


class PlatformDatabase:
    """Database engine and session manager for platform_postgres.
    
    This connection is optional - the game engine can function without it.
    If connection fails, adaptive difficulty will default to "medium".
    """

    def __init__(self):
        self.engine: AsyncEngine | None = None
        self.async_session: async_sessionmaker[AsyncSession] | None = None
        self.is_connected: bool = False

    async def connect(self):
        """Initialize engine and session factory.
        
        This is optional - if connection fails, the game engine will still work.
        """
        try:
            self.engine = create_async_engine(
                SETTINGS.platform_database_url,
                echo=False,  # Set to True for SQL logging
                pool_size=5,
                max_overflow=10,
            )

            self.async_session = async_sessionmaker(
                self.engine,
                class_=AsyncSession,
                expire_on_commit=False,
            )

            # Test connection
            async with self.async_session() as session:
                await session.execute(text("SELECT 1"))
            
            self.is_connected = True
            print("✓ Platform database engine connected")
        except Exception as e:
            self.is_connected = False
            print(f"⚠ Platform database connection failed (game will use default difficulty): {e}")

    async def disconnect(self):
        """Close the engine."""
        if self.engine:
            await self.engine.dispose()
            self.is_connected = False
            print("✓ Platform database engine disposed")

    @asynccontextmanager
    async def session(self) -> AsyncGenerator[AsyncSession, None]:
        """Get a database session.
        
        Raises:
            RuntimeError: If database is not connected
        """
        if not self.is_connected or self.engine is None:
            raise RuntimeError("Platform database is not connected")
        
        async with self.async_session() as session:
            try:
                yield session
                await session.commit()
            except Exception:
                await session.rollback()
                raise

    async def disconnect(self):
        """Close the engine."""
        if self.engine:
            await self.engine.dispose()
            print("✓ Platform database engine disposed")

    @asynccontextmanager
    async def session(self) -> AsyncGenerator[AsyncSession, None]:
        """Get a database session."""
        async with self.async_session() as session:
            try:
                yield session
                await session.commit()
            except Exception:
                await session.rollback()
                raise


# Global platform database instance
platform_db = PlatformDatabase()

