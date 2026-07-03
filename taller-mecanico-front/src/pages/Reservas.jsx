import { useState, useEffect } from 'react'
import { apiFetch } from '../api/api.js'

export default function Reservas() {
  const [reservas, setReservas] = useState([])
  const [formData, setFormData] = useState({
    motivo: '',
    fecha: '',
    mecanicoId: '1' // Por defecto el mecánico 1
  })
  const [loading, setLoading] = useState(false)
  const [loadingReservas, setLoadingReservas] = useState(true)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    fetchReservas()
  }, [])

  const fetchReservas = async () => {
    try {
      const data = await apiFetch('/api/reservas')
      setReservas(data)
    } catch (err) {
      console.error("Error al cargar reservas:", err)
    } finally {
      setLoadingReservas(false)
    }
  }

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSuccess('')
    setError('')
    setLoading(true)

    // Validar tipo de datos en Frontend según requerimiento
    if (formData.motivo.length < 10 || formData.motivo.length > 255) {
      setError("El motivo debe tener entre 10 y 255 caracteres.")
      setLoading(false)
      return
    }

    const selectedDate = new Date(formData.fecha)
    if (selectedDate <= new Date()) {
      setError("La fecha de reserva debe ser futura.")
      setLoading(false)
      return
    }

    try {
      await apiFetch('/api/reservas', {
        method: 'POST',
        body: JSON.stringify({
          motivo: formData.motivo,
          fecha: new Date(formData.fecha).toISOString(), // Formato estricto LocalDateTime
          mecanicoId: parseInt(formData.mecanicoId)
        })
      })
      setSuccess("Reserva creada exitosamente.")
      setFormData({ motivo: '', fecha: '', mecanicoId: '1' })
      fetchReservas() // Recargar lista
    } catch (err) {
      setError("Error al crear reserva: " + err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mt-4" style={{ maxWidth: 800 }}>
      <h2 className="mb-4 text-warning">Agendar Cita</h2>
      <p className="text-light">
        Registra una nueva cita del cliente: describe el motivo, elige fecha y asigna un mecánico.
        Luego podrás convertirla en orden de trabajo desde <strong>Citas</strong>.
      </p>

      <div className="row">
        <div className="col-md-6 mb-4">
          <div className="card shadow h-100">
            <div className="card-header fw-bold text-warning">Nueva Reserva</div>
            <div className="card-body">
              {error && <div className="alert alert-danger">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label text-light fw-bold">Motivo de Consulta</label>
              <textarea
                className="form-control bg-dark text-light"
                name="motivo"
                rows="3"
                value={formData.motivo}
                onChange={handleChange}
                placeholder="Ej. El motor hace ruido al acelerar..."
                required
              />
              <div className="form-text text-muted">Debe contener entre 10 y 255 caracteres.</div>
            </div>

            <div className="mb-3">
              <label className="form-label text-light fw-bold">Fecha y Hora</label>
              <input
                type="datetime-local"
                className="form-control bg-dark text-light"
                name="fecha"
                value={formData.fecha}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-4">
              <label className="form-label text-light fw-bold">Mecánico de Preferencia</label>
              <select
                className="form-select bg-dark text-light"
                name="mecanicoId"
                value={formData.mecanicoId}
                onChange={handleChange}
                required
              >
                <option value="1">Antonio "Toño" Ruiz (Motor y Transmisión)</option>
                <option value="2">Carlos "El Tuercas" (Frenos y Suspensión)</option>
                <option value="3">Luis Martínez (Electricidad Automotriz)</option>
              </select>
            </div>

            <button type="submit" className="btn btn-warning w-100 fw-bold" disabled={loading}>
              {loading ? 'Procesando...' : 'Confirmar Reserva'}
            </button>
          </form>
        </div>
      </div>
        </div>

        <div className="col-md-6 mb-4">
          <div className="card shadow h-100">
            <div className="card-header fw-bold text-warning">Últimas citas</div>
            <div className="card-body p-0">
              {loadingReservas ? (
                <div className="p-4 text-center"><div className="spinner-border text-warning"/></div>
              ) : reservas.length === 0 ? (
                <div className="empty-state m-3">📭 Aún no has agendado ninguna cita.<br/>Completa el formulario para reservar tu primera visita.</div>
              ) : (
                <ul className="list-group list-group-flush">
                  {reservas.map(r => (
                    <li key={r.id} className="list-group-item bg-dark text-light border-secondary">
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <strong className="text-info">{new Date(r.fecha).toLocaleString()}</strong>
                        <span className={`badge ${r.estado === 'Pendiente' ? 'bg-secondary' : 'bg-success'}`}>{r.estado}</span>
                      </div>
                      <div className="mb-1">{r.motivo}</div>
                      <small className="text-warning">Mecánico: {r.mecanicoNombre}</small>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
