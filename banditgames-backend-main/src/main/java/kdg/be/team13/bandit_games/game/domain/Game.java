package kdg.be.team13.bandit_games.game.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "games", schema = "TicTakToe")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private Board board;

    @Enumerated(EnumType.STRING)
    private Player currentPlayer;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Transient
    private Outcome outcome;

    @Transient
    private int moveNumber;

    protected Game() {}

    public Game(Board board, Player currentPlayer, GameStatus status, Outcome outcome, int moveNumber) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.status = status;
        this.outcome = outcome;
        this.moveNumber = moveNumber;
    }

    public UUID getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
}
