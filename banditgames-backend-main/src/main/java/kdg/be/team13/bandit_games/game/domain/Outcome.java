package kdg.be.team13.bandit_games.game.domain;

import jakarta.persistence.Embeddable;

public class Outcome {
    private final GameResult result;
    private final Player winner; // null if draw or none


    public Outcome(GameResult result, Player winner) {
        this.result = result;
        this.winner = winner;
    }


    public GameResult getResult() { return result; }
    public Player getWinner() { return winner; }
}
