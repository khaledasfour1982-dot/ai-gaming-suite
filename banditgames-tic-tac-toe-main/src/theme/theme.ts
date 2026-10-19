import { createTheme } from "@mui/material/styles"

declare module "@mui/material/styles" {
    interface Palette {
        neon: Palette["primary"]
        cyber: Palette["primary"]
    }
    interface PaletteOptions {
        neon?: PaletteOptions["primary"]
        cyber?: PaletteOptions["primary"]
    }
}

const theme = createTheme({
    palette: {
        mode: "dark",
        primary: {
            main: "#00d4ff",
            light: "#5ee4ff",
            dark: "#00a3cc",
        },
        secondary: {
            main: "#9000ff",
            light: "#b54dff",
            dark: "#6b00cc",
        },
        background: {
            default: "#0a0e27",
            paper: "#151b3d",
        },
        text: {
            primary: "#ffffff",
            secondary: "rgba(255, 255, 255, 0.7)",
        },
        neon: {
            main: "#00ffff",
            light: "#5fffff",
            dark: "#00cccc",
        },
        cyber: {
            main: "#ff00ff",
            light: "#ff5fff",
            dark: "#cc00cc",
        },
        error: {
            main: "#ff0055",
        },
        success: {
            main: "#00ff88",
        },
    },
    typography: {
        fontFamily: "'Rajdhani', 'Orbitron', 'Segoe UI', sans-serif",
        h1: {
            fontWeight: 900,
            letterSpacing: "0.3rem",
            textTransform: "uppercase",
        },
        h2: {
            fontWeight: 800,
            letterSpacing: "0.2rem",
        },
        h3: {
            fontWeight: 700,
            letterSpacing: "0.15rem",
        },
        h4: {
            fontWeight: 700,
            letterSpacing: "0.1rem",
        },
        button: {
            fontWeight: 900,
            letterSpacing: "0.15rem",
            textTransform: "uppercase",
        },
    },
    shape: {
        borderRadius: 8,
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 8,
                    padding: "12px 32px",
                    fontSize: "1rem",
                    boxShadow: "0 0 20px rgba(0, 212, 255, 0.3)",
                    transition: "all 0.3s ease",
                    "&:hover": {
                        boxShadow: "0 0 30px rgba(0, 212, 255, 0.6)",
                        transform: "translateY(-2px)",
                    },
                },
                contained: {
                    background: "linear-gradient(45deg, #00d4ff 0%, #9000ff 100%)",
                    "&:hover": {
                        background: "linear-gradient(45deg, #00d4ff 0%, #9000ff 100%)",
                        filter: "brightness(1.2)",
                    },
                },
                outlined: {
                    borderWidth: "2px",
                    borderColor: "#00d4ff",
                    "&:hover": {
                        borderWidth: "2px",
                        borderColor: "#00d4ff",
                        background: "rgba(0, 212, 255, 0.1)",
                    },
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    background: "rgba(21, 27, 61, 0.8)",
                    backdropFilter: "blur(10px)",
                    border: "1px solid rgba(0, 212, 255, 0.3)",
                    boxShadow: "0 0 20px rgba(0, 212, 255, 0.2)",
                    borderRadius: 12,
                },
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: "none",
                    background: "rgba(21, 27, 61, 0.9)",
                    border: "1px solid rgba(0, 212, 255, 0.2)",
                },
            },
        },
        MuiAppBar: {
            styleOverrides: {
                root: {
                    background: "rgba(10, 14, 39, 0.95)",
                    backdropFilter: "blur(10px)",
                    boxShadow: "0 2px 20px rgba(0, 212, 255, 0.2)",
                },
            },
        },
    },
})

export default theme