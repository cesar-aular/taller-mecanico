import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

// Ruta protegida (exigencia de la rúbrica):
// - Sin sesión -> redirige a /login.
// - Si la ruta exige ADMIN y el usuario no lo es -> lo devuelve al inicio.
export default function ProtectedRoute({ children, requireAdmin = false }) {
  const { user, isAdmin } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />
  }
  return children
}
