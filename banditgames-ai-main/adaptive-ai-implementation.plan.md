# Adaptive AI Implementation Plan

## Overview
Implement per-player win rate tracking that adjusts AI difficulty based on player performance:
- **<30% win rate** → Easy AI
- **30-60% win rate** → Medium AI  
- **>60% win rate** → Hard AI
- Requires **minimum 5 games** before adapting

**Architecture Note:** Platform database connection is **optional** - the game engine can function independently. If platform database is unavailable, adaptive difficulty defaults to "medium" so the game continues to work.

## Implementation Tasks

### ✅ Task 1: Connect to Platform Postgres for Player Stats
- [x] Add connection configuration for platform_postgres in `game_engine/config/settings.py`
- [x] Create `game_engine/infrastructure/platform_database.py` for platform DB connection
- [x] Use environment variable `PLATFORM_DATABASE_URL` (default: `postgresql+asyncpg://user:password@platform_postgres:5432/postgres`)
- [x] Create read-only connection to platform_postgres (same Docker network)
- [x] Initialize platform database connection in `game_engine/main.py` startup

### ✅ Task 2: Create Player Stats Repository
- [x] Create `game_engine/infrastructure/platform_player_repository.py`
- [x] Implement `get_player_stats(player_id: str, match_type: str = "HUMAN_VS_AI")` 
- [x] Query `kdg_platform.player_stats` table from platform_postgres
- [x] Filter by `player_id` (UUID) and `match_type`
- [x] Return: `wins`, `losses`, `draws`, `games_played`
- [x] Handle case where player doesn't exist in stats table (new player) → return None or default values

### ✅ Task 3: Create Adaptive Difficulty Service
- [x] Create `game_engine/service/adaptive_difficulty_service.py`
- [x] Implement `calculate_adaptive_difficulty(player_id: str) -> str`
- [x] Logic: Query player stats from platform_postgres, calculate win_rate = wins / games_played
- [x] Apply thresholds: <30% → "easy", 30-60% → "medium", >60% → "hard"
- [x] Return "medium" as default if games_played < 5 or player not found

### ✅ Task 4: Integrate Adaptive Difficulty in Session Creation
- [x] Modify `game_engine/api/controller/session_controller.py` in `start_game()`
- [x] If `vs_ai=True` and `ai_difficulty` is "adaptive" (or not provided), call adaptive service
- [x] Use calculated difficulty instead of fixed difficulty
- [x] Ensure backward compatibility (fixed difficulty like "easy", "medium", "hard" still works)
- [x] Fixed duplicate route handler in session_controller.py

### ✅ Task 5: Testing & Validation
- [ ] Test with new player (< 5 games → should default to medium)
- [ ] Test with player having <30% win rate → should get easy
- [ ] Test with player having 30-60% win rate → should get medium
- [ ] Test with player having >60% win rate → should get hard
- [ ] Verify connection to platform_postgres works from game_engine container

