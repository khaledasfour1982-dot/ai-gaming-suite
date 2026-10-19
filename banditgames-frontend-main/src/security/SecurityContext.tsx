import {createContext} from "react";
import Keycloak from "keycloak-js";

export interface SecurityContextType {
    keycloak: Keycloak;
    isInitialised: boolean;
    isAuthenticated: () => boolean;
    login: () => void;
    logout: () => void;
}

export const SecurityContext = createContext<SecurityContextType | undefined>(undefined);
