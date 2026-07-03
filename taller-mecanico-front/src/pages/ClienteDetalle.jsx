import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { apiFetch } from '../api/api.js'

// Vista ROLE_USER #2: "ver detalle" de un cliente.
// Consume GET /api/clientes/{id}; si no existe, el backend responde 404
// y aquí se muestra el mensaje de error controlado.
export default function ClienteDetalle() {
  const { id } = useParams()
  const [cliente, setCliente] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    apiFetch(`/api/clientes/${id}`)
      .then(setCliente)
      .catch((err) => setError(err.message))
  }, [id])

  if (error) {
    return (
      <div className="container">
        <div className="alert alert-warning">{error}</div>
        <Link to="/clientes" className="btn btn-secondary">Volver</Link>
      </div>
    )
  }

  if (!cliente) return <div className="container">Cargando...</div>

  return (
    <div className="container" style={{ maxWidth: 560 }}>
      <div className="card">
        <div className="card-header">Detalle del Cliente #{cliente.id}</div>
        <div className="card-body">
          <p><strong>Nombre:</strong> {cliente.nombreCompleto}</p>
          <p><strong>RUT:</strong> {cliente.rut}</p>
          <p><strong>Teléfono:</strong> {cliente.telefono || '—'}</p>
          <p><strong>Email:</strong> {cliente.email}</p>
          <Link to="/clientes" className="btn btn-secondary">Volver al listado</Link>
        </div>
      </div>
    </div>
  )
}
