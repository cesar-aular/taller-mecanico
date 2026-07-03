import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Configuración mínima de Vite: solo registra el plugin de React
// y fija el puerto 3000 (el que CorsConfig del backend ya espera).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
  },
})
