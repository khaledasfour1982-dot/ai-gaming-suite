package kdg.be.team13.bandit_games.platform.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {

    private final LobbyId id;
    private final GameId gameId;
    private final UUID hostId;

    private final List<UUID> players;

    private LobbyStatus status;

    private final int maxPlayers = 2;

    private Lobby(LobbyId id, GameId gameId, UUID hostId) {
        this.id = id;
        this.gameId = gameId;
        this.hostId = hostId;
        this.players = new ArrayList<>();
        this.players.add(hostId);
        this.status = LobbyStatus.WAITING;
    }

    public static Lobby create(UUID hostId, GameId gameId) {
        return new Lobby(LobbyId.newId(), gameId, hostId);
    }

    // ----- Domain behaviors -----

    public void join(UUID playerId) {
        if (status != LobbyStatus.WAITING)
            throw new IllegalStateException("Cannot join lobby that is not waiting.");

        if (players.size() >= maxPlayers)
            throw new IllegalStateException("Lobby is full.");

        if (players.contains(playerId))
            return; // already joined

        players.add(playerId);

        if (players.size() == maxPlayers) {
            status = LobbyStatus.READY;
        }
    }

    public void leave(UUID playerId) {
        if (!players.contains(playerId))
            return;

        players.remove(playerId);

        if (players.isEmpty()) {
            status = LobbyStatus.CANCELLED;
            return;
        }

        if (playerId.equals(hostId)) {
            // remove lobby probably after the host leaves
            status = LobbyStatus.CANCELLED;
            return;
        }

        if (players.size() < maxPlayers) {
            status = LobbyStatus.WAITING;
        }
    }

    public void startGame() {
        if (status != LobbyStatus.READY)
            throw new IllegalStateException("Cannot start unless lobby is READY.");

        status = LobbyStatus.STARTED;
    }


    // ----- Getters -----

    public LobbyId id() { return id; }
    public GameId gameId() { return gameId; }
    public LobbyStatus status() { return status; }
    public List<UUID> players() { return List.copyOf(players); }
}
