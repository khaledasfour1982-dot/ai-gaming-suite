package kdg.be.team13.bandit_games.game.service;

import kdg.be.team13.bandit_games.game.service.dto.GameState;
import kdg.be.team13.bandit_games.game.service.dto.MoveDTO;

public interface GameEngine {

    /**
     * Applies a move to the current game state and returns the updated state
     * (Python engine decides validity, nextPlayer, win/draw, etc.)
     */
    GameState applyMove(GameState state, MoveDTO move);
}
