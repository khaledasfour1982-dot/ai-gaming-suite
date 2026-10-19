import { useMutation } from "@tanstack/react-query";
import { getHint, type HintRequest } from "../services/hintService";
import type { Board } from "../model/gameplay/Board";

/**
 * Convert Board (with cells array) to 2D string array for ML API
 */
function boardToMatrix(board: Board, gridSize: number = 3): string[][] {
    // Create empty grid
    const matrix: string[][] = Array.from({ length: gridSize }, () =>
        Array(gridSize).fill("")
    );

    // Fill in the marks
    board.cells.forEach(cell => {
        if (cell.row < gridSize && cell.col < gridSize) {
            matrix[cell.row][cell.col] = cell.mark === "EMPTY" ? "" : cell.mark;
        }
    });

    return matrix;
}

export function useHint() {
    const {
        mutate: requestHint,
        data: hintData,
        isPending,
        isError,
        error,
        reset
    } = useMutation({
        mutationFn: ({ board, playerMark, gridSize }: { board: Board; playerMark: string; gridSize?: number }) => {
            const matrix = boardToMatrix(board, gridSize);
            const payload: HintRequest = {
                board: matrix,
                player_mark: playerMark
            };
            return getHint(payload);
        }
    });

    return {
        requestHint,
        hintData,
        isPending,
        isError,
        error,
        clearHint: reset
    };
}