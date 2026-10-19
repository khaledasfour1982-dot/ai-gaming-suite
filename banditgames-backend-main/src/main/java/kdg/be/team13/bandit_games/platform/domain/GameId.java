package kdg.be.team13.bandit_games.platform.domain;

import java.util.UUID;

public record GameId(UUID uuid) {
    public static GameId newId() { return new GameId(UUID.randomUUID()); }
}
