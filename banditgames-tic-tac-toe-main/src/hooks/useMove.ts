import { useMutation, useQueryClient } from "@tanstack/react-query";
import { playMove, type MovePayload } from "../services/moveService";
import type { Move } from "../model/gameplay/Move";

export function usePlayMove(gameSessionId: string) {  // ← USUŃ setWinProbability parametr!
    const queryClient = useQueryClient();

    const {
        mutate: move,
        isPending,
        isError,
        error
    } = useMutation({
        mutationFn: ({ row, col, player_id }: Move) => {
            const payload: MovePayload = {
                game_session_id: gameSessionId,
                player_id: player_id,
                move: {
                    row: row,
                    col: col
                }
            };
            return playMove(payload);
        },
        onSuccess: (data) => {
            queryClient.setQueryData(
                ["gameSession", gameSessionId],
                (old: any) => {
                    if (!old) return old;
                    return {
                        ...old,
                        win_probability: data.win_probability
                    };
                }
            );

            queryClient.invalidateQueries({
                queryKey: ["gameSession", gameSessionId],
            });
        },
    });

    return { move, isPending, isError, error };
}