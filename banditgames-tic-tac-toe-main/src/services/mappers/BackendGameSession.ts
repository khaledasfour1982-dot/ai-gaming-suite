export type BackendGameSession = {
    board: string[][];                         // [["", "X", ""], ...]
    current_player_id: string;                    // "X"
    current_player_mark: string;
    game_status: string;                       // "IN_PROGRESS"
    legal_moves: { row: number; col: number }[];
    outcome: any | null;
    win_probability?: number | null;
};
