/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: "#1e293b",  // ✅ Custom dark blue
                secondary: "#fbbf24",  // ✅ Custom yellow
            },
        },
    },
    plugins: [],
};