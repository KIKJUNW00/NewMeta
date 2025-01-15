/** @type {import('tailwindcss').Config} */
const animations = require('tailwindcss-animations');

module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      keyframes: {
        fadeout: {
          '0%': { opacity: '1' },
          '100%': { opacity: '0' },
        },
        fadein: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideup: {
          '0%': { transform: 'translateY(10%)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        faderight: {
          '0%': { transform: 'translateX(-10%)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
        FadeOutAndFadeRight:{
          '0%': { transform: 'translateX(-10%)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
          '0%': { opacity: '1' },
          '100%': { opacity: '0' },
        }
      },
      animation: {
        fadeout: 'fadeout 1.5s ease-in-out forwards',
        fadein: 'fadein 1.5s ease-in-out forwards',
        slideup: 'slideup 1.5s ease-in-out forwards',
        faderight: 'faderight 1.5s ease-in-out forwards',
      },
    },
  },
  plugins: [animations],
};
