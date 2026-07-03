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
        <Link className="navbar-brand" to="/">⚙️ Toño's Motors</Link>
        <div className="navbar-nav me-auto">
          {isAdmin && <Link className="nav-link" to="/clientes">Clientes</Link>}
          {isAdmin && <Link className="nav-link" to="/vehiculos">Vehículos</Link>}
          {isAdmin && <Link className="nav-link text-warning" to="/usuarios">Usuarios</Link>}
          {!isAdmin && <Link className="nav-link text-info" to="/reservas">Mis Reservas</Link>}
        </div>
        <span className="navbar-text me-3">
          {user.username}{' '}
          <span className={`badge ${isAdmin ? 'text-bg-warning' : 'text-bg-info'}`}>
            {isAdmin ? 'Admin' : 'User'}
          </span>
        </span>
        <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </div>
    </nav>
  )
}
