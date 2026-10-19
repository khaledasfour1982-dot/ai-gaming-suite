package kdg.be.team13.bandit_games.platform.port.in;

public record CreateGameCommand(
        String name,
        String description
) {}