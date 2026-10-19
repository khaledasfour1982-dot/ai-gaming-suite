# game_engine/infrastructure/database_sqlalchemy.py
"""
SQLAlchemy ORM implementation for game engine database.
"""

import os
from contextlib import asynccontextmanager
from datetime import datetime
from typing import AsyncGenerator
from uuid import UUID, uuid4

from sqlalchemy import String, Integer, DateTime, JSON, ForeignKey, UniqueConstraint
from sqlalchemy.ext.asyncio import (
    create_async_engine,
    async_sessionmaker,
    AsyncSession,
    AsyncEngine
)
from sqlalchemy.orm import DeclarativeBase


# Database URL (asyncpg driver)
DATABASE_URL = os.getenv(
    "DATABASE_URL","postgresql+asyncpg://postgres:postgres@localhost:5433/tictactoe"
)


# ============================================================================
# ORM BASE & ENGINE
# ============================================================================

class Base(DeclarativeBase):
    """Base class for all ORM models."""
    pass


class Database:
    """Database engine and session manager."""

    def __init__(self):
        self.engine: AsyncEngine | None = None
        self.async_session: async_sessionmaker[AsyncSession] | None = None

    async def connect(self):
        """Initialize engine and session factory."""
        self.engine = create_async_engine(
            DATABASE_URL,
            echo=False,  # Set to True for SQL logging
            pool_size=10,
            max_overflow=20,
        )

        self.async_session = async_sessionmaker(
            self.engine,
            class_=AsyncSession,
            expire_on_commit=False,
        )

        print("SQLAlchemy engine created")

    async def disconnect(self):
        """Close the engine."""
        if self.engine:
            await self.engine.dispose()
            print("✓ SQLAlchemy engine disposed")

    async def create_tables(self):
        """Create all tables if they don't exist."""
        async with self.engine.begin() as conn:
            await conn.run_sync(Base.metadata.create_all)
        print("✓ Database tables created")

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


# Global database instance
db = Database()


# ============================================================================
# ORM MODELS
# ============================================================================


# ============================================================================
# INITIALIZATION HELPER
# ============================================================================

async def init_database():
    await db.connect()

    async with db.engine.begin() as conn:
        # 1) Create schemas if missing
        await conn.run_sync(
            lambda sync_conn: sync_conn.exec_driver_sql(
                "CREATE SCHEMA IF NOT EXISTS kdg_tic_tac_toe"
            )
        )

        await conn.run_sync(
            lambda sync_conn: sync_conn.exec_driver_sql(
                "CREATE SCHEMA IF NOT EXISTS logging"
            )
        )

        # 2) Create all tables in those schemas
        await conn.run_sync(Base.metadata.create_all)

    print("✓ Database recreated from SQLAlchemy models")