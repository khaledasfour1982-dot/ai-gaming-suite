package kdg.be.team13.bandit_games.platform.adapter.out;

import kdg.be.team13.bandit_games.platform.domain.Achievement;
import kdg.be.team13.bandit_games.platform.domain.AchievementId;
import kdg.be.team13.bandit_games.platform.domain.Game;
import kdg.be.team13.bandit_games.platform.domain.GameId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class GameMapper {

    public GameJpaEntity toJpa(Game game) {
        GameJpaEntity entity = new GameJpaEntity(
                game.id().uuid(),
                game.name(),
                game.description(),
                new ArrayList<>()
        );

        List<AchievementJpaEntity> achievementEntities = game.achievements().stream()
                .map(a -> {
                    AchievementJpaEntity ae = new AchievementJpaEntity();
                    ae.setId(a.id().uuid());
                    ae.setGame(entity);
                    ae.setName(a.name());
                    ae.setDescription(a.description());
                    return ae;
                })
                .toList();

        entity.setAchievements(achievementEntities);
        return entity;
    }

    public Game toDomain(GameJpaEntity entity) {
        List<Achievement> achievements = entity.getAchievements().stream()
                .map(ae -> new Achievement(
                        new AchievementId(ae.getId()),
                        ae.getName(),
                        ae.getDescription()
                ))
                .toList();

        return new Game(
                new GameId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                achievements
        );
    }
}