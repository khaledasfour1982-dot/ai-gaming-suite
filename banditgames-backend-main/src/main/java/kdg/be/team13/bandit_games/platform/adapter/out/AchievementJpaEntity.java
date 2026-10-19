package kdg.be.team13.bandit_games.platform.adapter.out;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "achievements")
public class AchievementJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameJpaEntity game;

    private String name;
    private String description;

    // constructors, getters, setters


    public AchievementJpaEntity(UUID id, GameJpaEntity game, String name, String description) {
        this.id = id;
        this.game = game;
        this.name = name;
        this.description = description;
    }

    public AchievementJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GameJpaEntity getGame() {
        return game;
    }

    public void setGame(GameJpaEntity game) {
        this.game = game;
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
}