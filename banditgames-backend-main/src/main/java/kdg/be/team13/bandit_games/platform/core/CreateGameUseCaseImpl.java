package kdg.be.team13.bandit_games.platform.core;

import kdg.be.team13.bandit_games.platform.domain.Game;
import kdg.be.team13.bandit_games.platform.port.in.CreateGameCommand;
import kdg.be.team13.bandit_games.platform.port.in.CreateGameUseCase;
import kdg.be.team13.bandit_games.platform.port.out.SaveGamePort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreateGameUseCaseImpl implements CreateGameUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateGameUseCaseImpl.class);

    private final SaveGamePort saveGamePort;

    public CreateGameUseCaseImpl(SaveGamePort saveGamePort) {
        this.saveGamePort = saveGamePort;
    }

    @Override
    public void handle(CreateGameCommand command) {
        log.info("Creating game: {}, {}", command.name(), command.description());
        Game game = Game.create(command.name(), command.description());
        saveGamePort.save(game);
    }
}
