import { Box, Typography, LinearProgress } from "@mui/material";
import "./WinProbability.scss";

type WinProbabilityProps = {
    probability: number | null | undefined;
    playerMark: string;
};

function getColorClass(prob: number): string {
    if (prob < 0.25) return "veryLow";
    if (prob < 0.50) return "low";
    if (prob < 0.75) return "medium";
    return "high";
}

function getColorHex(prob: number): string {
    if (prob < 0.25) return "#ff0055"; // Red
    if (prob < 0.50) return "#ff6600"; // Orange
    if (prob < 0.75) return "#00d4ff"; // Cyan
    return "#00ff88"; // Green
}

export function WinProbability({ probability, playerMark }: WinProbabilityProps) {
    console.log("🎯 WINPROB COMPONENT RENDER:", { probability, playerMark });

    if (probability === null || probability === undefined) {
        console.log("🎯 WINPROB IS NULL/UNDEFINED - NOT RENDERING");
        return null;
    }

    const percentage = Math.round(probability * 100);
    const colorClass = getColorClass(probability);
    const colorHex = getColorHex(probability);

    console.log("🎯 WINPROB RENDERING:", { percentage, colorClass, colorHex });

    return (
        <Box className={`winProbability ${colorClass}`}>
            <Box className="winProbHeader">
                <Typography variant="overline" className="winProbLabel">
                    Win Probability
                </Typography>
                <Typography variant="h3" className="winProbValue">
                    {percentage}%
                </Typography>
            </Box>

            <Box className="winProbBarContainer">
                <LinearProgress
                    variant="determinate"
                    value={percentage}
                    className="winProbBar"
                    sx={{
                        '& .MuiLinearProgress-bar': {
                            backgroundColor: colorHex,
                            boxShadow: `0 0 20px ${colorHex}`,
                        }
                    }}
                />
            </Box>

            <Typography variant="caption" className="winProbPlayer">
                for {playerMark}
            </Typography>
        </Box>
    );
}