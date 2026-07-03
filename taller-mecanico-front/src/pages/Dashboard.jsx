import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { apiFetch } from '../api/api.js'

export default function Dashboard() {
  const { user, isAdmin } = useAuth()
  const [metrics, setMetrics] = useState(null)

  useEffect(() => {
    // Si queremos obtener metricas del backend
    apiFetch('/api/dashboard/metrics')
      .then(data => setMetrics(data))
      .catch(err => console.error("Error al cargar metricas:", err))
  }, [])

  return (
    <div className="container pb-5">
      
      {/* Hero Section */}
      <div className="row mb-4">
        <div className="col-12">
          <img src="/hero.jpg" alt="Taller Mecánico Toño's Motors" className="hero-img" />
          <h2 className="text-warning mt-3">Bienvenido a Toño's Motors, {user.username} 👋</h2>
          <p className="text-light">
            Especialistas en Motor, Transmisión y Electricidad Automotriz. <br/>
            Rol actual: <span className="badge bg-warning text-dark">{user.roles.join(', ')}</span> —{' '}
            {isAdmin
              ? 'Tienes acceso total al sistema (crear, editar, eliminar).'
              : 'Puedes explorar y reservar servicios.'}
          </p>
        </div>
      </div>

      {/* Metrics Section (Dashboard real) */}
      {metrics && (
        <div className="row mb-5 text-center">
          <div className="col-md-3">
            <div className="metric-card">
              <div className="metric-number">{metrics.totalVehiculos}</div>
              <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Vehículos Registrados</div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="metric-card">
              <div className="metric-number">{metrics.ordenesEntregados}</div>
              <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Trabajos Entregados</div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="metric-card">
              <div className="metric-number text-danger">{metrics.ordenesEnReparacion}</div>
              <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>En Reparación</div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="metric-card">
              <div className="metric-number text-info">{metrics.ordenesPendientes}</div>
              <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Servicios Pendientes</div>
            </div>
          </div>
        </div>
      )}

      {/* Acciones principales */}
      <div className="row g-4 mt-2">
        <div className="col-md-4">
          <div className="card h-100 border-warning">
            <div className="card-body text-center">
              <h1 className="display-4 mb-3">👤</h1>
              <h5 className="card-title">Gestión de Clientes</h5>
              <p className="card-text mb-4">Administra el listado y detalle de los clientes del taller. Mantén el contacto.</p>
              <Link to="/clientes" className="btn btn-outline-warning w-100">Ver clientes</Link>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card h-100 border-warning">
            <div className="card-body text-center">
              <h1 className="display-4 mb-3">🚗</h1>
              <h5 className="card-title">Vehículos y Flota</h5>
              <p className="card-text mb-4">Consulta los vehículos registrados y su respectivo dueño o empresa.</p>
              <Link to="/vehiculos" className="btn btn-outline-warning w-100">Ver vehículos</Link>
            </div>
          </div>
        </div>
        
        {/* User extra options requested by prompt */}
        {!isAdmin && (
          <div className="col-md-4">
            <div className="card h-100 border-warning">
              <div className="card-body text-center">
                <h1 className="display-4 mb-3">📅</h1>
                <h5 className="card-title">Agendar Cita</h5>
                <p className="card-text mb-4">Solicita un turno de servicio y elige a tu mecánico de preferencia (Toño, Carlos, etc).</p>
                <button className="btn btn-warning w-100" onClick={() => alert("Módulo de reservas en construcción.")}>Reservar ahora</button>
              </div>
            </div>
          </div>
        )}
        
        {isAdmin && (
          <div className="col-md-4">
            <div className="card h-100 border-warning">
              <div className="card-body text-center">
                <h1 className="display-4 mb-3">📋</h1>
                <h5 className="card-title">Órdenes de Trabajo</h5>
                <p className="card-text mb-4">Revisa el estado de todas las reparaciones, mecánicos asignados y repuestos.</p>
                <button className="btn btn-warning w-100" onClick={() => alert("Módulo de órdenes en construcción.")}>Ver Órdenes</button>
              </div>
            </div>
          </div>
        )}
      </div>

    </div>
  )
}
