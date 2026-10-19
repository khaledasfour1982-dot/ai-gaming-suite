import {Button} from "@mui/material";
import "./Cell.scss";

type CellProps = {
    row: number;
    col: number;
    mark: string;
    onClick: () => void;
    isHighlighted?: boolean;
};

export function Cell({row, col, mark, onClick, isHighlighted = false}: CellProps) {
    const isFilled = mark !== "EMPTY";
    const displayMark = isFilled ? mark : "\u00A0";

    return (
        <Button
            className={`boardCell ${isHighlighted ? 'highlighted' : ''}`}
            onClick={onClick}
            disabled={isFilled}
            aria-label={`Cell ${row + 1}, ${col + 1}`}
            variant="outlined"
            fullWidth
            sx={isHighlighted ? {
                backgroundColor: '#ffeb3b',
                '&:hover': {
                    backgroundColor: '#fdd835',
                },
                animation: 'pulse 1.5s ease-in-out infinite',
                '@keyframes pulse': {
                    '0%, 100%': {
                        boxShadow: '0 0 0 0 rgba(255, 235, 59, 0.7)',
                    },
                    '50%': {
                        boxShadow: '0 0 0 10px rgba(255, 235, 59, 0)',
                    },
                },
            } : {}}
        >
            {displayMark}
        </Button>
    );
}