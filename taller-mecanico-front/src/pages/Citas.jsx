import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiFetch } from '../api/api.js'

// Vista del personal (USER=secretaria y ADMIN): todas las citas agendadas.
// La secretaria puede CONVERTIR una cita en una orden de trabajo, asignándole el
// vehículo que ingresa al taller (POST /api/reservas/{id}/convertir).
export default function Citas() {
  const [citas, setCitas] = useState([])
  const [vehiculos, setVehiculos] = useState([])
  const [convertId, setConvertId] = useState(null)     // cita que se está convirtiendo
  const [vehiculoId, setVehiculoId] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [costo, setCosto] = useState('')

  const cargar = () => {
    apiFetch('/api/reservas')
      .then(setCitas)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    cargar()
    apiFetch('/api/vehiculos').then(setVehiculos).catch(() => {})
  }, [])

  const abrirConversion = (cita) => {
    setError(''); setMsg('')
    setConvertId(cita.id)
    setVehiculoId(vehiculos[0] ? String(vehiculos[0].id) : '')
    setCosto('')
  }

  const convertir = async () => {
    if (!vehiculoId) { setError('Selecciona un vehículo.'); return }
    if (costo !== '' && Number(costo) < 0) { setError('El costo no puede ser negativo.'); return }
    setError(''); setMsg('')
    try {
      const res = await apiFetch(`/api/reservas/${convertId}/convertir`, {
        method: 'POST',
        // costoTotal es opcional: si se deja vacío se envía 0 (se ajusta luego en la orden)
        body: JSON.stringify({ vehiculoId: Number(vehiculoId), costoTotal: Number(costo) || 0 }),
      })
      setMsg(res.mensaje)
      setConvertId(null)
      setCosto('')
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const badge = (estado) => {
    if (estado === 'Convertida') return 'text-bg-success'
    return 'text-bg-secondary'
  }

  return (
    <div className="container pb-5">
      <h2>Citas Agendadas</h2>
      <p className="text-muted">
        Reservas del taller. La secretaría recepciona el vehículo y convierte la cita en una orden de trabajo.
      </p>
      {error && <div className="alert alert-danger">{error}</div>}
      {msg && (
        <div className="alert alert-success d-flex justify-content-between align-items-center">
          <span>{msg}</span>
          <Link to="/ordenes" className="btn btn-sm btn-outline-warning">Ver órdenes</Link>
        </div>
      )}

      {/* Panel de conversión */}
      {convertId && (
        <div className="card border-warning mb-3">
          <div className="card-body">
            <h5 className="card-title text-warning">Convertir cita #{convertId} en orden de trabajo</h5>
            <p className="text-muted mb-3">Recepciona el vehículo y define el costo estimado de la reparación:</p>
            <div className="row g-2 align-items-end">
              <div className="col-md-6">
                <label className="form-label small text-muted mb-1">Vehículo que ingresa</label>
                <select className="form-select" value={vehiculoId} onChange={(e) => setVehiculoId(e.target.value)}>
                  {vehiculos.map((v) => (
                    <option key={v.id} value={v.id}>
                      {v.patente} — {v.marca} {v.modelo} ({v.clienteNombre})
                    </option>
                  ))}
                </select>
              </div>
              <div className="col-md-3">
                <label className="form-label small text-muted mb-1">Costo estimado (CLP)</label>
                <input
                  type="number"
                  min="0"
                  step="1000"
                  className="form-control"
                  placeholder="0"
                  value={costo}
                  onChange={(e) => setCosto(e.target.value)}
                />
              </div>
              <div className="col-md-3 d-flex gap-2">
                <button className="btn btn-warning fw-bold flex-fill" onClick={convertir}>Crear orden</button>
                <button className="btn btn-outline-secondary" onClick={() => setConvertId(null)}>Cancelar</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {loading ? (
        <div className="text-center py-5"><div className="spinner-border text-warning" /></div>
      ) : citas.length === 0 ? (
        <div className="empty-state">Aún no hay citas agendadas.</div>
      ) : (
        <div className="table-panel">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Fecha y hora</th>
                  <th>Agendada por</th>
                  <th>Motivo</th>
                  <th>Mecánico</th>
                  <th>Estado</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {citas.map((c) => (
                  <tr key={c.id}>
                    <td className="text-muted">{c.id}</td>
                    <td>{new Date(c.fecha).toLocaleString('es-CL')}</td>
                    <td><span className="badge bg-secondary">{c.cliente}</span></td>
                    <td style={{ maxWidth: 280 }}>{c.motivo}</td>
                    <td>{c.mecanicoNombre}</td>
                    <td><span className={`badge ${badge(c.estado)}`}>{c.estado}</span></td>
                    <td>
                      {c.estado === 'Convertida' ? (
                        <span className="text-muted small">→ orden creada</span>
                      ) : (
                        <button className="btn btn-sm btn-warning" onClick={() => abrirConversion(c)}>
                          Convertir a orden
                        </button>
                      )}
                    </td>
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
