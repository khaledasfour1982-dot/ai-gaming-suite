package kdg.be.team13.bandit_games.game.service;

import kdg.be.team13.bandit_games.game.service.dto.GameState;

public record EngineGameResponse(
        GameState state
) {
}
