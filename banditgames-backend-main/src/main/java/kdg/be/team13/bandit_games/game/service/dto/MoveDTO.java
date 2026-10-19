package kdg.be.team13.bandit_games.game.service.dto;

import kdg.be.team13.bandit_games.game.domain.Move;

public record MoveDTO(
        int row,
        int col
) {

    public static MoveDTO from (int row, int col) {
        return new MoveDTO(row, col);
    }
}
