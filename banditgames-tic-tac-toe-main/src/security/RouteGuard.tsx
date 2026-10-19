import {type PropsWithChildren, useContext, useEffect} from "react";
import {SecurityContext} from "./SecurityContext.tsx";

export function RouteGuard({children}: PropsWithChildren) {
    const security = useContext(SecurityContext);

    if (!security) throw new Error("RouteGuard must be used inside SecurityContextProvider");

    const {isInitialised, isAuthenticated, login} = security;

    useEffect(() => {
        if (isInitialised && !isAuthenticated()) {
            login();
        }
    }, [isInitialised, isAuthenticated, login]);

    if (!isAuthenticated()) {
        return <div>Authenticating...</div>;
    }

    return <>{children}</>;
}
