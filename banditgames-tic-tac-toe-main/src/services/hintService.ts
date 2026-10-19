import axios from "axios";

const ML_API_BASE_URL = "http://localhost:8010";

export interface HintRequest {
    board: string[][];
    player_mark: string;
}

export interface HintResponse {
    row: number;
    col: number;
    class_id: number;
    confidence: number;
    raw_score: number;
}

export async function getHint(payload: HintRequest): Promise<HintResponse> {
    const response = await axios.post(`${ML_API_BASE_URL}/ml/predict_move`, payload);
    return response.data;
}