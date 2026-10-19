package kdg.be.team13.bandit_games.platform.adapter.out;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameJpaEntity {
    @Id
    private UUID id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AchievementJpaEntity> achievements = new ArrayList<>();

    public GameJpaEntity(UUID id, String name, String description, List<AchievementJpaEntity> achievements) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.achievements = achievements;
    }

    public GameJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AchievementJpaEntity> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementJpaEntity> achievements) {
        this.achievements = achievements;
    }
}
