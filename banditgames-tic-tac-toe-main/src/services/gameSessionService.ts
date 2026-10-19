import axios from "axios";
import type { GameSession } from "../model/gameplay/GameSession";
import {mapBackendSessionToFrontend} from "./mappers/gameSessionMapper.ts";

axios.defaults.baseURL = import.meta.env.VITE_BACKEND_URL;
axios.defaults.withCredentials = true;

export type AiDifficulty = "EASY" | "MEDIUM" | "HARD" | "NONE";

export interface StartSessionPayload {
    game_session_id: string;      // generated on frontend
    player1_id: string;
    player2_id?: string | null;
    vs_ai: boolean;
    ai_difficulty?: string;
}

// GET /api/session/{gameSessionId}
export async function getGameSession(id: string): Promise<GameSession> {
    try {
        const response = await axios.get(`/session/${id}`);
        return mapBackendSessionToFrontend(id, response.data);
    } catch (err) {
        console.error("Error fetching restaurant:", err);
        throw err;
    }
}

// POST /api/session/start
export async function startGameSession(
    payload: StartSessionPayload
): Promise<GameSession> {
    const response = await axios.post("/session/start", payload);
    return response.data;
}
