import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import {
    Container, Box, Typography, Card, CardContent,
    Alert, CircularProgress, Button
} from "@mui/material";
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

import { BoardGrid } from "../components/board/BoardGrid";
import { WinProbability } from "../components/board/WinProbability";
import { useGameSession, useStartGameSession } from "../hooks/useGameSession";
import { usePlayMove } from "../hooks/useMove";
import { useHint } from "../hooks/useHint";

export function GameSessionPage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const player1Id = searchParams.get("player1Id");
    const player2Id = searchParams.get("player2Id");
    const vsAI = searchParams.get("vsAI") === "true";
    const gameSessionId = searchParams.get("gameSessionId");
    const aiDifficulty = searchParams.get("aiDifficulty");

    const [sessionId, setSessionId] = useState<string | undefined>();
    const [attemptedStart, setAttemptedStart] = useState(false);
    const [highlightedCell, setHighlightedCell] = useState<{ row: number; col: number } | null>(null);

    const { startGame, isPending: isStarting } = useStartGameSession();
    const { data: gameSession, isLoading, isError, error } = useGameSession(sessionId);
    const { move, isPending: isMovePending, isError: moveError } = usePlayMove(sessionId ?? "");
    const { requestHint, hintData, isPending: isHintPending, isError: isHintError, clearHint } = useHint();

    useEffect(() => {
        if (!gameSessionId) return;
        if (!sessionId) {
            setSessionId(gameSessionId);
            return;
        }

        const status = (error as any)?.response?.status;
        if (isError && status === 404 && !attemptedStart) {
            setAttemptedStart(true);
            startGame({
                game_session_id: sessionId,
                player1_id: player1Id!,
                player2_id: player2Id || null,
                vs_ai: vsAI,
                ai_difficulty: aiDifficulty?.toUpperCase() ?? "NONE"
            });
        }
    }, [
        sessionId,
        gameSessionId,
        isError,
        error,
        attemptedStart,
        startGame,
        player1Id,
        player2Id,
        vsAI,
        aiDifficulty
    ]);

    // Update highlighted cell when hint data arrives
    useEffect(() => {
        if (hintData) {
            setHighlightedCell({ row: hintData.row, col: hintData.col });
            // Auto-clear highlight after 5 seconds
            const timer = setTimeout(() => {
                setHighlightedCell(null);
                clearHint();
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [hintData, clearHint]);

    if ((!gameSession && isLoading) || isStarting) {
        return (
            <Container className="pageState">
                <CircularProgress />
                <Typography variant="h6" sx={{ mt: 2 }}>
                    Loading game session...
                </Typography>
            </Container>
        );
    }

    if (isError && attemptedStart) {
        return (
            <Container className="pageState">
                <Typography variant="h6" color="error">
                    Unable to load or create game session.
                </Typography>
            </Container>
        );
    }

    if (!gameSession) return null;

    const handleCellClick = (row: number, col: number) => {
        if (!sessionId || isMovePending) return;
        // Clear hint when user makes a move
        setHighlightedCell(null);
        clearHint();
        move({ row, col, player_id: gameSession.current_player.uuid });
    };

    const handleHintClick = () => {
        if (!gameSession) return;

        // Calculate grid size dynamically
        const gridSize = gameSession.board.cells.length > 0
            ? Math.max(
            Math.max(...gameSession.board.cells.map(c => c.row)),
            Math.max(...gameSession.board.cells.map(c => c.col))
        ) + 1
            : 3;

        requestHint({
            board: gameSession.board,
            playerMark: gameSession.current_player.mark,
            gridSize
        });
    };

    return (
        <Container maxWidth="xl" className="page">
            <Box className="header">
                <Button
                    variant="outlined"
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate('http://localhost:3000/games')}
                    sx={{ mb: 2 }}
                >
                    Go Back
                </Button>
                <Typography variant="h1" className="title">
                    Current turn: {gameSession.current_player.mark}
                </Typography>
                <Typography variant="body2" className="status">
                    {gameSession.game_status}
                </Typography>
            </Box>

            {moveError && (
                <Alert severity="error" className="errorText">
                    Unable to submit move. Try again.
                </Alert>
            )}

            {isHintError && (
                <Alert severity="error" className="errorText">
                    Unable to get hint. Try again.
                </Alert>
            )}

            <Box className="layout">
                <Card className="playerPanel playerLeft">
                    <CardContent>
                        <Typography variant="overline" className="playerLabel">
                            Player 1
                        </Typography>
                        <Typography variant="h2" className="playerMark">
                            X
                        </Typography>
                    </CardContent>
                </Card>

                <Box className="boardPanel">
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<LightbulbIcon />}
                        onClick={handleHintClick}
                        disabled={isHintPending || isMovePending || gameSession.game_status !== "IN_PROGRESS"}
                        sx={{ mb: 2, width: '100%' }}
                    >
                        {isHintPending ? "Getting Hint..." : "Get Hint"}
                    </Button>

                    <BoardGrid
                        board={gameSession.board}
                        onCellClick={handleCellClick}
                        highlightedCell={highlightedCell}
                    />

                    <WinProbability
                        probability={gameSession.win_probability}
                        playerMark={gameSession.current_player.mark}
                    />
                </Box>

                <Card className="playerPanel playerRight">
                    <CardContent>
                        <Typography variant="overline" className="playerLabel">
                            Player 2
                        </Typography>
                        <Typography variant="h2" className="playerMark">
                            O
                        </Typography>
                    </CardContent>
                </Card>
            </Box>
        </Container>
    );
}