import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { apiFetch } from '../api/api.js'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'

ChartJS.register(ArcElement, Tooltip, Legend)

export default function Dashboard() {
  const { user, isAdmin } = useAuth()
  const [metrics, setMetrics] = useState(null)

  useEffect(() => {
    // Las métricas del negocio son datos internos: solo el ADMIN las carga.
    // El endpoint /api/dashboard/** está restringido a ROLE_ADMIN en el backend.
    if (!isAdmin) return
    apiFetch('/api/dashboard/metrics')
      .then(data => setMetrics(data))
      .catch(err => console.error("Error al cargar metricas:", err))
  }, [isAdmin])

  return (
    <div className="container pb-5">
      
      {/* Hero Section */}
      <div className="row mb-4">
        <div className="col-12">
          <img src="/hero.jpg" alt="Taller Mecánico Toño's Motors" className="hero-img" />
          <h2 className="text-warning mt-3">Bienvenido a Toño's Motors, {user.username} 👋</h2>
          <p className="text-light">
            Especialistas en Motor, Transmisión y Electricidad Automotriz. <br/>
            Rol actual: <span className="badge bg-warning text-dark">
              {user.roles.map(r => r.replace('ROLE_', '')).join(', ')}
            </span> —{' '}
            {isAdmin
              ? 'Jefe de taller: acceso total (crear, editar, eliminar, usuarios y métricas del negocio).'
              : 'Secretaría: agenda citas, recepciona vehículos y convierte citas en órdenes de trabajo.'}
          </p>
        </div>
      </div>

      {/* Metrics Section (Dashboard real) */}
      {metrics && (
        <div className="row mb-5 align-items-center">
          <div className="col-md-8">
            <div className="row text-center">
              <div className="col-md-6 mb-4">
                <div className="metric-card">
                  <div className="metric-number">{metrics.totalVehiculos}</div>
                  <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Vehículos Registrados</div>
                </div>
              </div>
              <div className="col-md-6 mb-4">
                <div className="metric-card">
                  <div className="metric-number">{metrics.ordenesEntregados}</div>
                  <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Trabajos Entregados</div>
                </div>
              </div>
              <div className="col-md-6">
                <div className="metric-card">
                  <div className="metric-number text-danger">{metrics.ordenesEnReparacion}</div>
                  <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>En Reparación</div>
                </div>
              </div>
              <div className="col-md-6">
                <div className="metric-card">
                  <div className="metric-number text-info">{metrics.ordenesPendientes}</div>
                  <div className="text-light text-uppercase fw-bold" style={{fontSize: '0.8rem'}}>Servicios Pendientes</div>
                </div>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="card border-warning">
              <div className="card-body text-center">
                <h5 className="card-title mb-3">Estado de Servicios</h5>
                <div style={{ height: '250px', display: 'flex', justifyContent: 'center' }}>
                  <Doughnut 
                    data={{
                      labels: ['Pendientes', 'En Reparación', 'Listos', 'Entregados'],
                      datasets: [{
                        data: [
                          metrics.ordenesPendientes, 
                          metrics.ordenesEnReparacion, 
                          metrics.ordenesListos, 
                          metrics.ordenesEntregados
                        ],
                        backgroundColor: ['#0dcaf0', '#dc3545', '#198754', '#ffc107'],
                        borderWidth: 0
                      }]
                    }}
                    options={{ maintainAspectRatio: false, plugins: { legend: { labels: { color: '#fff' } } } }}
                  />
                </div>
              </div>
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
        
        {/* Órdenes de trabajo: lo ven ambos roles */}
        <div className="col-md-4">
          <div className="card h-100 border-warning">
            <div className="card-body text-center">
              <h1 className="display-4 mb-3">📋</h1>
              <h5 className="card-title">Órdenes de Trabajo</h5>
              <p className="card-text mb-4">Estado de las reparaciones, mecánicos asignados y repuestos.</p>
              <Link to="/ordenes" className="btn btn-outline-warning w-100">Ver órdenes</Link>
            </div>
          </div>
        </div>

        {/* Secretaría (USER): agenda citas y las convierte en órdenes */}
        {!isAdmin && (
          <>
            <div className="col-md-4">
              <div className="card h-100 border-warning">
                <div className="card-body text-center">
                  <h1 className="display-4 mb-3">🗂️</h1>
                  <h5 className="card-title">Citas Agendadas</h5>
                  <p className="card-text mb-4">Recepciona el vehículo y convierte la cita en una orden de trabajo.</p>
                  <Link to="/citas" className="btn btn-warning w-100">Gestionar citas</Link>
                </div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card h-100 border-warning">
                <div className="card-body text-center">
                  <h1 className="display-4 mb-3">📅</h1>
                  <h5 className="card-title">Agendar Cita</h5>
                  <p className="card-text mb-4">Registra una nueva cita eligiendo motivo, fecha y mecánico.</p>
                  <Link to="/reservas" className="btn btn-warning w-100">Agendar ahora</Link>
                </div>
              </div>
            </div>
          </>
        )}

        {/* Jefe (ADMIN): administración de usuarios */}
        {isAdmin && (
          <div className="col-md-4">
            <div className="card h-100 border-warning">
              <div className="card-body text-center">
                <h1 className="display-4 mb-3">👥</h1>
                <h5 className="card-title">Usuarios y Roles</h5>
                <p className="card-text mb-4">Administra las cuentas del personal y sus privilegios de acceso.</p>
                <Link to="/usuarios" className="btn btn-warning w-100">Gestionar usuarios</Link>
              </div>
            </div>
          </div>
        )}
      </div>

    </div>
  )
}
