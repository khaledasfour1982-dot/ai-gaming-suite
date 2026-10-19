import type {Board} from "./Board";
import type {Player} from "./Player";
// import type {Outcome} from "./Outcome";

export type GameSession = {
    id: number,
    board: Board; // direct 2D array from backend
    current_player: Player;
    game_status: string;
    legal_moves: { row: number; col: number }[];
    outcome: null | {
        winner?: "X" | "O" | "DRAW";
    };
    win_probability?: number | null;
};
