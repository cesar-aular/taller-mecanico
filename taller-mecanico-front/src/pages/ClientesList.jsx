import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiFetch } from '../api/api.js'
import { useAuth } from '../auth/AuthContext.jsx'

// Vista ROLE_USER #1: listado de clientes (datos reales desde la API).
// Adaptación por rol (rúbrica): los botones Nuevo/Editar/Eliminar
// solo se renderizan si el usuario es ADMIN.
export default function ClientesList() {
  const { isAdmin } = useAuth()
  const [clientes, setClientes] = useState([])
  const [error, setError] = useState('')

  const cargar = () => {
    apiFetch('/api/clientes')
      .then(setClientes)
      .catch((err) => setError(err.message))
  }

  useEffect(cargar, [])

  const eliminar = async (id) => {
    if (!window.confirm('¿Eliminar este cliente?')) return
    try {
      await apiFetch(`/api/clientes/${id}`, { method: 'DELETE' })
      cargar() // recarga la tabla tras el 204
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="container">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Clientes</h2>
        {isAdmin && (
          <Link to="/clientes/nuevo" className="btn btn-success">+ Nuevo cliente</Link>
        )}
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      <table className="table table-striped table-hover">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>RUT</th>
            <th>Email</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.nombreCompleto}</td>
              <td>{c.rut}</td>
              <td>{c.email}</td>
              <td>
                <Link to={`/clientes/${c.id}`} className="btn btn-sm btn-outline-primary me-2">
                  Ver detalle
                </Link>
                {isAdmin && (
                  <>
                    <Link to={`/clientes/${c.id}/editar`} className="btn btn-sm btn-outline-secondary me-2">
                      Editar
                    </Link>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(c.id)}>
                      Eliminar
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
