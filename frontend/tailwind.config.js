/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        'health-primary': '#0f172a', // Slate 900
        'health-secondary': '#e2e8f0', // Slate 200
        'health-teal': '#0d9488', // Teal 600
        'health-bg': '#f8fafc', // Slate 50
      }
    },
  },
  plugins: [],
}

