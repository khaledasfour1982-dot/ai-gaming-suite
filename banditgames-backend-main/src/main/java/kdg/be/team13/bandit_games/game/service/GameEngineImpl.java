package kdg.be.team13.bandit_games.game.service;

import kdg.be.team13.bandit_games.game.service.dto.GameState;
import kdg.be.team13.bandit_games.game.service.dto.MoveDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GameEngineImpl implements GameEngine {
    private final RestTemplate rest = new RestTemplate();
    private static final String ENGINE_URL = "http://localhost:8000/api/engine/move";

    @Override
    public GameState applyMove(GameState state, MoveDTO move) {

        EngineApplyMoveRequest request = new EngineApplyMoveRequest(state, move);

        EngineGameResponse response = rest.postForObject(
                ENGINE_URL,
                request,
                EngineGameResponse.class
        );

        return response.state();
    }
}
