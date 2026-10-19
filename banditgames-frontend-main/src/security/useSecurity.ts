import {useContext} from "react";
import {SecurityContext} from "./SecurityContext";

export function useSecurity() {
    const context = useContext(SecurityContext);
    if (!context) {
        throw new Error("useSecurity hook must be used within a SecurityContextProvider");
    }
    return context;
}
