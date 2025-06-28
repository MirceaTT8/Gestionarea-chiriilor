// tailwind.config.js
module.exports = {
    content: [
        "./index.html", // Scan your main HTML file
        "./src/**/*.{vue,js,ts,jsx,tsx}", // Scan all Vue/JS/TS files in the src directory
    ],

    // darkMode: media, // or 'media' or 'class'
    theme: {
        extend: {},
    },
    variants: {
        extend: {},
    },
    plugins: [],
}