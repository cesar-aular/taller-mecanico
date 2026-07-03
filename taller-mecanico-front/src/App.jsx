import { Routes, Route, Navigate } from 'react-router-dom'
import NavBar from './components/NavBar.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Login from './pages/Login.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ClientesList from './pages/ClientesList.jsx'
import ClienteDetalle from './pages/ClienteDetalle.jsx'
import ClienteForm from './pages/ClienteForm.jsx'
import VehiculosList from './pages/VehiculosList.jsx'
import VehiculoForm from './pages/VehiculoForm.jsx'
import Register from './pages/Register.jsx'
import UserManagement from './pages/UserManagement.jsx'
import Reservas from './pages/Reservas.jsx'

// Mapa de rutas de la SPA.
// - /login es pública.
// - Las demás exigen sesión (ProtectedRoute).
// - Las de creación/edición exigen además ROLE_ADMIN (requireAdmin).
export default function App() {
  return (
    <>
      <NavBar />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        
        <Route path="/reservas" element={<ProtectedRoute><Reservas /></ProtectedRoute>} />
        <Route path="/usuarios" element={<ProtectedRoute requireAdmin><UserManagement /></ProtectedRoute>} />

        <Route path="/clientes" element={<ProtectedRoute><ClientesList /></ProtectedRoute>} />
        <Route path="/clientes/nuevo" element={<ProtectedRoute requireAdmin><ClienteForm /></ProtectedRoute>} />
        <Route path="/clientes/:id" element={<ProtectedRoute><ClienteDetalle /></ProtectedRoute>} />
        <Route path="/clientes/:id/editar" element={<ProtectedRoute requireAdmin><ClienteForm /></ProtectedRoute>} />

        <Route path="/vehiculos" element={<ProtectedRoute><VehiculosList /></ProtectedRoute>} />
        <Route path="/vehiculos/nuevo" element={<ProtectedRoute requireAdmin><VehiculoForm /></ProtectedRoute>} />
        <Route path="/vehiculos/:id/editar" element={<ProtectedRoute requireAdmin><VehiculoForm /></ProtectedRoute>} />

        {/* Cualquier otra ruta vuelve al inicio */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      
      {/* Footer corporativo Toño's Motors */}
      <footer className="footer mt-auto">
        <div className="container text-center">
          <p className="mb-1 fw-bold text-warning">© 2026 Toño's Motors. Todos los derechos reservados.</p>
          <p className="mb-0 text-muted small">
            📍 Av. Los Tacos 123, Ciudad de Motores | 📞 +56 9 1234 5678 | ✉️ contacto@tonosmotors.cl
          </p>
        </div>
      </footer>
    </>
  )
}
