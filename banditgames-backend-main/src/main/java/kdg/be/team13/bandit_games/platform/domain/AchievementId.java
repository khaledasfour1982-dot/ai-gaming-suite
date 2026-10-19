package kdg.be.team13.bandit_games.platform.domain;

import java.util.UUID;

public record AchievementId(UUID uuid) {
    public static AchievementId newId() { return new AchievementId(UUID.randomUUID()); }
}
