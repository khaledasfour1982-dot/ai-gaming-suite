import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'   // or vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [react()], // or [vue()]
    server: {
        port: 5174,
    },
})
