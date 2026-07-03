import { useState } from 'react'
import { apiFetch } from '../api/api.js'

export default function Reservas() {
  const [formData, setFormData] = useState({
    motivo: '',
    fecha: '',
    mecanicoId: '1' // Por defecto el mecánico 1
  })
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

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
    } catch (err) {
      setError("Error al crear reserva: " + err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 600 }}>
      <h2 className="mb-4 text-warning">Agendar Cita en Toño's Motors</h2>
      <p className="text-light">
        Selecciona la fecha y describe el problema de tu vehículo. Asignaremos al mecánico disponible o al de tu preferencia.
      </p>

      <div className="card shadow border-info mt-4">
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
                <option value="1">Carlos "El Tuercas" López (Especialista en Motor)</option>
                <option value="2">Luis Martínez (Especialista en Frenos)</option>
                <option value="3">Roberto Gómez (Especialista Eléctrico)</option>
              </select>
            </div>

            <button type="submit" className="btn btn-info w-100 fw-bold" disabled={loading}>
              {loading ? 'Procesando...' : 'Confirmar Reserva'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
