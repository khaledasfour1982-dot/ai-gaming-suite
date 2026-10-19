package kdg.be.team13.bandit_games.platform.domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final GameId id;
    private String name;
    private String description;

    private final List<Achievement> achievements;

    public Game(GameId id, String name, String description, List<Achievement> achievements) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.achievements = achievements;
    }

    public static Game create(String name, String description) {
        return new Game(
                GameId.newId(),
                name,
                description,
                new ArrayList<>()
        );
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    public List<Achievement> achievements() {
        return List.copyOf(achievements);
    }

    public GameId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
}
