package kdg.be.team13.bandit_games.platform.domain;

import java.util.UUID;

public record LobbyId(UUID uuid) {
    public static LobbyId newId() {
        return new LobbyId(UUID.randomUUID());
    }
}
