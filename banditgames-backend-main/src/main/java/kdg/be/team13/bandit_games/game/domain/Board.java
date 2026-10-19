package kdg.be.team13.bandit_games.game.domain;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;

import java.util.List;


@Embeddable
public class Board {
    @ElementCollection
    @CollectionTable(name = "board_cells", joinColumns = @JoinColumn(name = "game_id"))
    private List<Cell> board;

    public Board(List<Cell> board) {
        this.board = board;
    }

    public Board() {
    }

    public List<Cell> getBoard() {
        return board;
    }
}
