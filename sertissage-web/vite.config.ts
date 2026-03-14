import path from "path"
import react from "@vitejs/plugin-react"
import { defineConfig } from "vite"

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "https://miniature-space-goldfish-9795q5wq96gvcj59-8080.app.github.dev/",
        changeOrigin: true,
        secure: false,
      },
    },
  },
})