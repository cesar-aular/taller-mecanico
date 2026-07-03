import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { apiFetch } from '../api/api.js'

// Formulario ADMIN de creación/edición de vehículos.
// El dueño se elige desde un <select> cargado con los clientes reales de la API,
// así el usuario no puede tipear una FK inexistente (y si igual pasara,
// el backend responde 404 "Cliente no encontrado" y se muestra aquí).
export default function VehiculoForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [form, setForm] = useState({ patente: '', marca: '', modelo: '', anio: '', clienteId: '' })
  const [clientes, setClientes] = useState([])
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})

  useEffect(() => {
    apiFetch('/api/clientes')
      .then(setClientes)
      .catch((err) => setError(err.message))

    if (id) {
      apiFetch(`/api/vehiculos/${id}`)
        .then((v) => setForm({
          patente: v.patente,
          marca: v.marca,
          modelo: v.modelo,
          anio: v.anio || '',
          clienteId: v.clienteId,
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
    const body = JSON.stringify({
      ...form,
      anio: form.anio ? Number(form.anio) : null,
      clienteId: Number(form.clienteId),
    })
    try {
      if (id) {
        await apiFetch(`/api/vehiculos/${id}`, { method: 'PUT', body })
      } else {
        await apiFetch('/api/vehiculos', { method: 'POST', body })
      }
      navigate('/vehiculos')
    } catch (err) {
      setError(err.message)
      if (err.fields) setFieldErrors(err.fields)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 560 }}>
      <h2>{id ? `Editar Vehículo #${id}` : 'Nuevo Vehículo'}</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Patente</label>
          <input
            name="patente"
            className={`form-control ${fieldErrors.patente ? 'is-invalid' : ''}`}
            value={form.patente}
            onChange={handleChange}
            placeholder="ABCD-12"
            required
          />
          {fieldErrors.patente && <div className="invalid-feedback">{fieldErrors.patente}</div>}
        </div>
        <div className="mb-3">
          <label className="form-label">Marca</label>
          <input
            name="marca"
            className={`form-control ${fieldErrors.marca ? 'is-invalid' : ''}`}
            value={form.marca}
            onChange={handleChange}
            required
          />
          {fieldErrors.marca && <div className="invalid-feedback">{fieldErrors.marca}</div>}
        </div>
        <div className="mb-3">
          <label className="form-label">Modelo</label>
          <input
            name="modelo"
            className={`form-control ${fieldErrors.modelo ? 'is-invalid' : ''}`}
            value={form.modelo}
            onChange={handleChange}
            required
          />
          {fieldErrors.modelo && <div className="invalid-feedback">{fieldErrors.modelo}</div>}
        </div>
        <div className="mb-3">
          <label className="form-label">Año</label>
          <input
            name="anio"
            type="number"
            className="form-control"
            value={form.anio}
            onChange={handleChange}
            min="1950"
            max="2030"
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Cliente dueño</label>
          <select
            name="clienteId"
            className={`form-select ${fieldErrors.clienteId ? 'is-invalid' : ''}`}
            value={form.clienteId}
            onChange={handleChange}
            required
          >
            <option value="">-- Seleccione un cliente --</option>
            {clientes.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombreCompleto} ({c.rut})
              </option>
            ))}
          </select>
          {fieldErrors.clienteId && <div className="invalid-feedback">{fieldErrors.clienteId}</div>}
        </div>
        <button className="btn btn-primary me-2">Guardar</button>
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/vehiculos')}>
          Cancelar
        </button>
      </form>
    </div>
  )
}
