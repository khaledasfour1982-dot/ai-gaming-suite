import axios from "axios";

axios.defaults.baseURL = import.meta.env.VITE_BACKEND_URL;
axios.defaults.withCredentials = true;

export interface MovePayload {
    game_session_id: string;      // generated on frontend
    player_id: string;
    move: {
        row: number,
        col: number
    }
}

export async function playMove(
    payload: MovePayload
) {
    const response = await axios.post(`/game/move`, payload);
    return response.data;
}

