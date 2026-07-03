import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

// Pantalla de inicio tras el login: puertas de entrada a las secciones.
export default function Dashboard() {
  const { user, isAdmin } = useAuth()

  return (
    <div className="container">
      <h2>Bienvenido, {user.username} 👋</h2>
      <p className="text-muted">
        Rol: {user.roles.join(', ')} —{' '}
        {isAdmin
          ? 'puedes crear, editar y eliminar registros.'
          : 'puedes consultar la información del taller.'}
      </p>
      <div className="row g-3 mt-2">
        <div className="col-md-6">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title">👤 Clientes</h5>
              <p className="card-text">Listado y detalle de los clientes del taller.</p>
              <Link to="/clientes" className="btn btn-primary">Ver clientes</Link>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title">🚗 Vehículos</h5>
              <p className="card-text">Vehículos registrados y su dueño.</p>
              <Link to="/vehiculos" className="btn btn-primary">Ver vehículos</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
