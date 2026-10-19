package kdg.be.team13.bandit_games.platform.adapter.out;

import kdg.be.team13.bandit_games.platform.domain.Game;
import kdg.be.team13.bandit_games.platform.port.out.SaveGamePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameJpaAdapter implements SaveGamePort {

    private final GameJpaRepository repository;
    private final GameMapper mapper;

    public GameJpaAdapter(GameJpaRepository repository, GameMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Game game) {
        GameJpaEntity entity = mapper.toJpa(game);
        repository.save(entity);
    }
}