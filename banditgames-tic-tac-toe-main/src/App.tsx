import {Routes, Route, Navigate} from "react-router-dom";
import {RouteGuard} from "./security/RouteGuard.tsx";
import {GameSessionPage} from "./pages/GameSessionPage.tsx";


export default function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/game" replace />} />
            <Route path="/game" element={<RouteGuard><GameSessionPage/></RouteGuard>} />
        </Routes>
    );
}