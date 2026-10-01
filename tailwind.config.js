/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        dark: {
          950: '#07090D',
          900: '#0B0E14',
          850: '#10141C',
          800: '#161B26',
          750: '#1E2534',
          700: '#2A3346',
          600: '#3D4961',
        },
        gold: {
          400: '#FBBF24',
          500: '#F59E0B',
          600: '#D97706',
          glow: 'rgba(245, 158, 11, 0.15)',
        },
        profit: {
          400: '#34D399',
          500: '#10B981',
          600: '#059669',
          glow: 'rgba(16, 185, 129, 0.15)',
        },
        loss: {
          400: '#F87171',
          500: '#EF4444',
          600: '#DC2626',
          glow: 'rgba(239, 68, 68, 0.15)',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
