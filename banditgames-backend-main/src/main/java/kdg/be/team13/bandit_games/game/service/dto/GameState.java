package kdg.be.team13.bandit_games.game.service.dto;

import kdg.be.team13.bandit_games.game.domain.Cell;
import kdg.be.team13.bandit_games.game.domain.Game;
import kdg.be.team13.bandit_games.game.domain.Move;

import java.util.ArrayList;
import java.util.List;

public record GameState(
        List<List<String>> board,
        String currentPlayer,
        List<MoveDTO> legalMoves,
        String gameStatus
) {

    public static GameState from(final Game game, final List<Move> legalMoves) {
        return new GameState(
                convertBoard(game.getBoard().getBoard()),
                game.getCurrentPlayer().name(),
                legalMoves.stream().map(move -> MoveDTO.from(move.getRow(), move.getCol())).toList(),
                game.getStatus().name()
        );
    }

    private static List<List<String>> convertBoard(List<Cell> cells) {
        List<List<String>> board = new ArrayList<>();

        // Convert flat list to 3x3 grid
        for (int row = 0; row < 3; row++) {
            List<String> rowData = new ArrayList<>();
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                Cell cell = cells.get(index);

                String value = switch (cell.getValue()) {
                    case X -> "X";
                    case O -> "O";
                    case EMPTY -> "";
                };
                rowData.add(value);
            }
            board.add(rowData);
        }

        return board;
    }
}