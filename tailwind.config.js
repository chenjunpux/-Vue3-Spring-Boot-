import daisyui from 'daisyui'

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: ['class', '[data-theme="dark"]'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Noto Sans SC', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'sans-serif'],
      },
    },
  },
  plugins: [daisyui],
  daisyui: {
    themes: [
      {
        light: {
          'primary': '#3b82f6',
          'secondary': '#6366f1',
          'accent': '#f59e0b',
          'neutral': '#1f2937',
          'base-100': '#ffffff',
          'base-200': '#f8fafc',
          'base-300': '#e5e7eb',
          'info': '#06b6d4',
          'success': '#22c55e',
          'warning': '#f59e0b',
          'error': '#ef4444',
        },
        dark: {
          'primary': '#60a5fa',
          'secondary': '#818cf8',
          'accent': '#fbbf24',
          'neutral': '#1f2937',
          'base-100': '#111827',
          'base-200': '#1f2937',
          'base-300': '#374151',
          'info': '#22d3ee',
          'success': '#4ade80',
          'warning': '#fbbf24',
          'error': '#f87171',
        },
      },
    ],
  },
}
