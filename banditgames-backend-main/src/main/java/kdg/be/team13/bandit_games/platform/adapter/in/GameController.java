package kdg.be.team13.bandit_games.platform.adapter.in;

import kdg.be.team13.bandit_games.platform.adapter.in.requests.CreateGameRequest;
import kdg.be.team13.bandit_games.platform.port.in.CreateGameCommand;
import kdg.be.team13.bandit_games.platform.port.in.CreateGameUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

    private final CreateGameUseCase createGame;

    public GameController(CreateGameUseCase createGame) {
        this.createGame = createGame;
    }

    @PostMapping
    public void createGame(@RequestBody CreateGameRequest request) {
        createGame.handle(new CreateGameCommand(request.name(), request.description()));
    }

}
