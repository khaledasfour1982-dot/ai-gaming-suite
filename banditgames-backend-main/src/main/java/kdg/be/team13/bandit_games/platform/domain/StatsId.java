package kdg.be.team13.bandit_games.platform.domain;

import java.util.UUID;

public record StatsId(UUID playerId, GameId gameId) {}
