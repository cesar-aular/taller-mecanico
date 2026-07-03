import { useEffect, useState } from 'react'
import { apiFetch } from '../api/api.js'

// Vista ADMIN: listado de órdenes de trabajo (reparaciones) del taller.
// Consume GET /api/ordenes, endpoint exclusivo de ROLE_ADMIN.
export default function Ordenes() {
  const [ordenes, setOrdenes] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiFetch('/api/ordenes')
      .then(setOrdenes)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  // Color del badge según el estado de la reparación
  const badge = (estado) => {
    switch (estado) {
      case 'Entregado': return 'text-bg-warning'
      case 'Listo': return 'text-bg-success'
      case 'En reparación': return 'text-bg-danger'
      default: return 'text-bg-secondary' // Pendiente
    }
  }

  const clp = (n) => (n ? '$' + Number(n).toLocaleString('es-CL') : '—')

  return (
    <div className="container pb-5">
      <h2>Órdenes de Trabajo</h2>
      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div className="text-center py-5"><div className="spinner-border text-warning" /></div>
      ) : ordenes.length === 0 ? (
        <div className="empty-state">No hay órdenes de trabajo registradas.</div>
      ) : (
        <div className="table-panel">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Vehículo</th>
                  <th>Problema</th>
                  <th>Mecánico</th>
                  <th>Repuestos</th>
                  <th>Estado</th>
                  <th className="text-end">Costo</th>
                </tr>
              </thead>
              <tbody>
                {ordenes.map((o) => (
                  <tr key={o.id}>
                    <td className="text-muted">{o.id}</td>
                    <td>
                      <div className="fw-semibold">{o.vehiculo}</div>
                      <small className="text-muted">{o.patente}</small>
                    </td>
                    <td style={{ maxWidth: 260 }}>{o.descripcion}</td>
                    <td>{o.mecanico}</td>
                    <td>
                      {o.repuestos && o.repuestos.length > 0
                        ? o.repuestos.map((r, i) => (
                            <span key={i} className="badge bg-secondary me-1 mb-1">{r}</span>
                          ))
                        : <span className="text-muted">—</span>}
                    </td>
                    <td><span className={`badge ${badge(o.estado)}`}>{o.estado}</span></td>
                    <td className="text-end fw-semibold">{clp(o.costoTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
