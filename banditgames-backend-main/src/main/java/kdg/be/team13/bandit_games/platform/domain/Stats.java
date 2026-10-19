package kdg.be.team13.bandit_games.platform.domain;

public class Stats {

    private final StatsId id;

    private int gamesPlayed;
    private int wins;
    private int losses;
    private int draws;

    public Stats(StatsId id) {
        this.id = id;
    }

    public void recordWin() {
        gamesPlayed++;
        wins++;
    }

    public void recordLoss() {
        gamesPlayed++;
        losses++;
    }

    public void recordDraw() {
        gamesPlayed++;
        draws++;
    }

    public double winRate() {
        return gamesPlayed == 0 ? 0 :
                (double) wins / gamesPlayed;
    }

    public StatsId id() {
        return id;
    }

    public int gamesPlayed() {
        return gamesPlayed;
    }

    public int wins() {return wins;}
    public int losses() {return losses;}
    public int draws() {return draws;}
}