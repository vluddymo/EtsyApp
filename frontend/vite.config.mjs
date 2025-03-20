import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [
        react(),
    ],
    server: {
        port: 3000,
        cors: true,
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
                secure: false,
            }
        }
    },
    root: ".",  // Ensures Vite looks in the right place
    build: {
        outDir: "dist",
    },
    base: "/",  // Ensures correct asset paths in production
});