import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react()],
    server: {
        port: 3000,
    },
    root: ".",  // Ensures Vite looks in the right place
    build: {
        outDir: "dist",
    },
    base: "./",  // Ensures correct asset paths in production
});