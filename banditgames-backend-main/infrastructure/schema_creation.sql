CREATE SCHEMA IF NOT EXISTS kdg_platform;

CREATE TABLE IF NOT EXISTS kdg_platform.games (
                       id UUID PRIMARY KEY,
                       name TEXT NOT NULL,
                       description TEXT
);

CREATE TABLE IF NOT EXISTS kdg_platform.achievements (
                              id UUID PRIMARY KEY,
                              game_id UUID NOT NULL REFERENCES kdg_platform.games(id) ON DELETE CASCADE,
                              name TEXT NOT NULL,
                              description TEXT
);