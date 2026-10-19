import {Box} from "@mui/material";
import type {Board} from "../../model/gameplay/Board.ts";
import type {Cell as CellModel} from "../../model/gameplay/Cell.ts";
import {Cell} from "./Cell.tsx";
import "./BoardGrid.scss";

type BoardGridProps = {
    board: Board;
    onCellClick: (row: number, col: number) => void;
    highlightedCell?: { row: number; col: number } | null;
};

function toGrid(cells: CellModel[]) {
    if (cells.length === 0) return [];
    // Compute dynamic grid size
    const maxRow = Math.max(...cells.map(c => c.row));
    const maxCol = Math.max(...cells.map(c => c.col));
    const gridSize = Math.max(maxRow, maxCol) + 1; // +1 because rows/cols are 0-indexed

    const cellMap = new Map<string, CellModel>();
    cells.forEach(cell => cellMap.set(`${cell.row}-${cell.col}`, cell));

    return Array.from({ length: gridSize }, (_, row) =>
        Array.from({ length: gridSize }, (_, col) => {
            const existing = cellMap.get(`${row}-${col}`);
            return existing ?? { row, col, mark: "EMPTY" };
        })
    );
}

export function BoardGrid({board, onCellClick, highlightedCell}: BoardGridProps) {
    const grid = toGrid(board.cells);

    const isHighlighted = (row: number, col: number) => {
        return highlightedCell?.row === row && highlightedCell?.col === col;
    };

    return (
        <Box className="boardGrid">
            {grid.flat().map((cell) => (
                <Cell
                    key={`${cell.row}-${cell.col}`}
                    row={cell.row}
                    col={cell.col}
                    mark={cell.mark}
                    onClick={() => onCellClick(cell.row, cell.col)}
                    isHighlighted={isHighlighted(cell.row, cell.col)}
                />
            ))}
        </Box>
    );
}