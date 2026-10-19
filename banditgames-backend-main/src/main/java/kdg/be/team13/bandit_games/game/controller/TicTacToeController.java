package kdg.be.team13.bandit_games.game.controller;

import kdg.be.team13.bandit_games.game.service.GameService;
import kdg.be.team13.bandit_games.game.service.dto.MoveDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tictactoe")
public class TicTacToeController {

    private final GameService service;


    public TicTacToeController(GameService service) {
        this.service = service;
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<Void> playMove(@PathVariable UUID id, @RequestBody MoveDTO move) {
        service.makeMove(id,move.col(), move.row());

        return ResponseEntity.ok().build();
    }

}

