import type { BackendGameSession } from "./BackendGameSession";
import type { GameSession } from "../../model/gameplay/GameSession";
import type { Board } from "../../model/gameplay/Board";
import type { Cell } from "../../model/gameplay/Cell";
import type { Player } from "../../model/gameplay/Player";

/**
 * Convert backend 3x3 array of strings to Board.cells[]
 */
function mapBoard(board2D: string[][]): Board {
    const cells: Cell[] = board2D.flatMap((row, r) =>
        row.map((mark, c) => ({
            row: r,
            col: c,
            mark: mark === "" ? "EMPTY" : mark,
        }))
    );

    return { cells };
}

/**
 * Convert backend player mark to Player object
 */
function mapPlayer(uuid: string, mark: string): Player {
    return { uuid, mark };
}

/**
 * Main mapper: BackendGameSession → GameSession
 */
export function mapBackendSessionToFrontend(
    id: string,
    backend: BackendGameSession
): GameSession {
    return {
        id: Number(id),
        board: mapBoard(backend.board),
        current_player: mapPlayer(backend.current_player_id, backend.current_player_mark),
        game_status: backend.game_status,
        legal_moves: backend.legal_moves,
        outcome: backend.outcome,
        win_probability: backend.win_probability,
    };
}
