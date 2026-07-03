import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { apiFetch } from '../api/api.js'

// Formulario ADMIN de creación y edición de clientes (misma vista para ambos).
// Validación integrada (criterio 5 de la rúbrica):
//  - React valida campos requeridos antes de enviar (atributo required).
//  - El backend valida de nuevo (@NotBlank, @Email, RUT duplicado) y aquí
//    se muestra su mensaje de error (400 con mapa de campos, o 409).
export default function ClienteForm() {
  const { id } = useParams() // si hay id, estamos editando
  const navigate = useNavigate()
  const [form, setForm] = useState({ nombreCompleto: '', rut: '', telefono: '', email: '' })
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})

  // Modo edición: precarga los datos actuales del cliente
  useEffect(() => {
    if (id) {
      apiFetch(`/api/clientes/${id}`)
        .then((c) => setForm({
          nombreCompleto: c.nombreCompleto,
          rut: c.rut,
          telefono: c.telefono || '',
          email: c.email,
        }))
        .catch((err) => setError(err.message))
    }
  }, [id])

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    try {
      if (id) {
        await apiFetch(`/api/clientes/${id}`, { method: 'PUT', body: JSON.stringify(form) })
      } else {
        await apiFetch('/api/clientes', { method: 'POST', body: JSON.stringify(form) })
      }
      navigate('/clientes')
    } catch (err) {
      setError(err.message)
      if (err.fields) setFieldErrors(err.fields) // errores por campo del backend
    }
  }

  return (
    <div className="container" style={{ maxWidth: 560 }}>
      <h2>{id ? `Editar Cliente #${id}` : 'Nuevo Cliente'}</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Nombre completo</label>
          <input
            name="nombreCompleto"
            className={`form-control ${fieldErrors.nombreCompleto ? 'is-invalid' : ''}`}
            value={form.nombreCompleto}
            onChange={handleChange}
            required
          />
          {fieldErrors.nombreCompleto && (
            <div className="invalid-feedback">{fieldErrors.nombreCompleto}</div>
          )}
        </div>
        <div className="mb-3">
          <label className="form-label">RUT</label>
          <input
            name="rut"
            className={`form-control ${fieldErrors.rut ? 'is-invalid' : ''}`}
            value={form.rut}
            onChange={handleChange}
            placeholder="11111111-1"
            required
          />
          {fieldErrors.rut && <div className="invalid-feedback">{fieldErrors.rut}</div>}
        </div>
        <div className="mb-3">
          <label className="form-label">Teléfono</label>
          <input
            name="telefono"
            className="form-control"
            value={form.telefono}
            onChange={handleChange}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            name="email"
            type="email"
            className={`form-control ${fieldErrors.email ? 'is-invalid' : ''}`}
            value={form.email}
            onChange={handleChange}
            required
          />
          {fieldErrors.email && <div className="invalid-feedback">{fieldErrors.email}</div>}
        </div>
        <button className="btn btn-primary me-2">Guardar</button>
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/clientes')}>
          Cancelar
        </button>
      </form>
    </div>
  )
}
