package kdg.be.team13.bandit_games.game.domain;


import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Cell {
    private int row;
    private int col;

    @Enumerated(EnumType.STRING)
    private Mark value;


    public Cell(int row, int col, Mark value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public Cell() {

    }

    public Mark getValue() {
        return value;
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
