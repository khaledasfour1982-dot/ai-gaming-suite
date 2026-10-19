package kdg.be.team13.bandit_games.platform.port.out;

import kdg.be.team13.bandit_games.platform.domain.Game;

public interface SaveGamePort {
    void save(Game game);
}