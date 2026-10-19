package kdg.be.team13.bandit_games.platform.domain;

public class Achievement {
    private final AchievementId id;
    private String name;
    private String description;

    public Achievement(AchievementId id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Achievement create(String name, String description) {
        return new Achievement(AchievementId.newId(), name, description);
    }

    public AchievementId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
}
