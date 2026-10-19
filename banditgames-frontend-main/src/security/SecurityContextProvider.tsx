import {type PropsWithChildren, useEffect, useState} from "react";
import Keycloak from "keycloak-js";
import axios from "axios";
import {SecurityContext} from "./SecurityContext";
import {addAccessTokenToAuthHeader, removeAccessTokenFromAuthHeader,} from "../services/auth";

const keycloakConfig = {
    url: import.meta.env.VITE_KC_URL,
    realm: import.meta.env.VITE_KC_REALM,
    clientId: import.meta.env.VITE_KC_CLIENT_ID,
};

const keycloak = new Keycloak(keycloakConfig);
const backendUrl = import.meta.env.VITE_BACKEND_URL;

export function SecurityContextProvider({children}: PropsWithChildren) {
    const [isInitialised, setIsInitialised] = useState(false);

    useEffect(() => {
        const initKeycloak = async () => {
            try {
                const authenticated = await keycloak.init({
                    onLoad: "check-sso",
                    checkLoginIframe: false,
                });

                if (authenticated && keycloak.token) {
                    addAccessTokenToAuthHeader(keycloak.token);

                    try {
                        await axios.post(
                            `${backendUrl}/api/players`,
                            {},
                            {headers: {Authorization: `Bearer ${keycloak.token}`}}
                        );
                    } catch (error) {
                        console.error("Owner registration failed:", error);
                    }
                }

                setIsInitialised(true);
            } catch (error) {
                console.error("Keycloak initialization failed:", error);
            }
        };

        initKeycloak();
    }, []);

    const login = () => keycloak.login();
    const logout = () => {
        removeAccessTokenFromAuthHeader();
        keycloak.logout({redirectUri: window.location.origin});
    };

    const isAuthenticated = () => keycloak.authenticated;

    return (
        <SecurityContext.Provider
            value={{keycloak, isInitialised, isAuthenticated, login, logout}}
        >
            {children}
        </SecurityContext.Provider>
    );
}
