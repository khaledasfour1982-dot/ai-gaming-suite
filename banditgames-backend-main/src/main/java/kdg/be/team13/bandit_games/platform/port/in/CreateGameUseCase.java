package kdg.be.team13.bandit_games.platform.port.in;

public interface CreateGameUseCase {
    void handle(CreateGameCommand command);
}