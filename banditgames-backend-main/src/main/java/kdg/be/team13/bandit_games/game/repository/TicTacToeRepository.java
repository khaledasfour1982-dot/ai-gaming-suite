package kdg.be.team13.bandit_games.game.repository;

import kdg.be.team13.bandit_games.game.domain.Game;
import kdg.be.team13.bandit_games.game.service.GameEngine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicTacToeRepository extends JpaRepository<Game, UUID> {
}
