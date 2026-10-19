package kdg.be.team13.bandit_games.game.service;

import kdg.be.team13.bandit_games.game.service.dto.GameState;
import kdg.be.team13.bandit_games.game.service.dto.MoveDTO;

public record EngineApplyMoveRequest(
        GameState state,
        MoveDTO move
) {
}
