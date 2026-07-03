import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

// Barra de navegación (rúbrica): muestra el usuario autenticado, su rol
// y el botón de cerrar sesión.
export default function NavBar() {
  const { user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  if (!user) return null

  return (
    <nav className="navbar navbar-expand navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">🔧 Taller Mecánico</Link>
        <div className="navbar-nav me-auto">
          <Link className="nav-link" to="/clientes">Clientes</Link>
          <Link className="nav-link" to="/vehiculos">Vehículos</Link>
        </div>
        <span className="navbar-text me-3">
          {user.username}{' '}
          <span className={`badge ${isAdmin ? 'text-bg-warning' : 'text-bg-info'}`}>
            {isAdmin ? 'ADMIN' : 'USER'}
          </span>
        </span>
        <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </div>
    </nav>
  )
}
