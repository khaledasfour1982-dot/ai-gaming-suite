import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider, CssBaseline } from "@mui/material";
import App from "./App";
import { SecurityContextProvider } from "./security/SecurityContextProvider";
import theme from "./theme/theme";

const root = createRoot(document.getElementById("root")!);
const qc = new QueryClient();

root.render(
    <ThemeProvider theme={theme}>
        <CssBaseline />
        <QueryClientProvider client={qc}>
            <SecurityContextProvider>
                <BrowserRouter>
                    <App />
                </BrowserRouter>
            </SecurityContextProvider>
        </QueryClientProvider>
    </ThemeProvider>
);