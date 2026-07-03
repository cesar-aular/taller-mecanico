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

        <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />

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
    </>
  )
}
