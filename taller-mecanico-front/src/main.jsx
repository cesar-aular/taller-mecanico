import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
// Framework CSS exigido por la rúbrica: Bootstrap (importado desde node_modules)
import 'bootstrap/dist/css/bootstrap.min.css'
import App from './App.jsx'
import { AuthProvider } from './auth/AuthContext.jsx'

// Punto de entrada: monta la app dentro del <div id="root"> de index.html.
// BrowserRouter habilita React Router y AuthProvider comparte la sesión
// (token + usuario) con todos los componentes.
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)
