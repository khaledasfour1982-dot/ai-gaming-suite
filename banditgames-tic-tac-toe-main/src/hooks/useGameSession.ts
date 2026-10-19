import { useQuery } from "@tanstack/react-query";
import { getGameSession } from "../services/gameSessionService";
import {useMutation} from "@tanstack/react-query";
import {startGameSession, type StartSessionPayload} from "../services/gameSessionService.ts";


export function useGameSession(id?: string) {
    return useQuery({
        queryKey: ["gameSession", id],
        queryFn: () => getGameSession(id!),
        enabled: !!id,
        retry: false,
        refetchOnWindowFocus: true
    });
}


export function useStartGameSession() {

    const {
        mutate: startGame,
        isPending,
        isError,
        error
    } = useMutation({
        mutationFn: (payload: StartSessionPayload) => startGameSession(payload)
    });

    return {startGame, isPending, isError, error};
}
