package kdg.be.team13.bandit_games.game.service;

import kdg.be.team13.bandit_games.game.domain.Board;
import kdg.be.team13.bandit_games.game.domain.Cell;
import kdg.be.team13.bandit_games.game.domain.Game;
import kdg.be.team13.bandit_games.game.domain.Mark;
import kdg.be.team13.bandit_games.game.domain.Move;
import kdg.be.team13.bandit_games.game.domain.Player;
import kdg.be.team13.bandit_games.game.exceptions.GameNotFoundException;
import kdg.be.team13.bandit_games.game.repository.TicTacToeRepository;
import kdg.be.team13.bandit_games.game.service.dto.GameState;
import kdg.be.team13.bandit_games.game.service.dto.MoveDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final TicTacToeRepository repository;
    private final GameEngine gameEngine; // port to Python game engine (not used yet here)

    public GameService(TicTacToeRepository repository, GameEngine gameEngine) {
        this.repository = repository;
        this.gameEngine = gameEngine;
    }

    /**
     * Returns the current game state (board + currentPlayer + legal moves)
     * for the given game id.
     */
    public void makeMove(UUID gameId, int col, int row) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game with id: " + gameId + " not found!"));

        Player currentPlayer = game.getCurrentPlayer();

        List<Move> legalMoves = getLegalMoves(currentPlayer, game.getBoard());

        GameState updatedState = gameEngine
                .applyMove(GameState.from(game, legalMoves), MoveDTO.from(col, row));

        System.out.println(updatedState + "idk");
    }

//    private void updateGameState(Game game, GameState gameState) {
//
//    }

    /**
     * Computes all legal moves for the given player on the given board.
     * A legal move is any EMPTY cell on the 3x3 grid.
     */
    private List<Move> getLegalMoves(Player currentPlayer, Board board) {
        List<Cell> grid = board.getBoard();
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < grid.size(); i++) {
            Cell cell = grid.get(i);
            if (cell.getValue() == Mark.EMPTY) {
                // Convert linear index to row/col
                int row = cell.getRow();
                int col = cell.getCol();
                moves.add(new Move(col, row, currentPlayer));
            }
        }
        return moves;
    }
}
