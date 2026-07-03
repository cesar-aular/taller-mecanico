import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiFetch } from '../api/api.js'
import { useAuth } from '../auth/AuthContext.jsx'

// Listado de vehículos con el nombre de su dueño (segunda entidad del CRUD).
export default function VehiculosList() {
  const { isAdmin } = useAuth()
  const [vehiculos, setVehiculos] = useState([])
  const [error, setError] = useState('')

  const cargar = () => {
    apiFetch('/api/vehiculos')
      .then(setVehiculos)
      .catch((err) => setError(err.message))
  }

  useEffect(cargar, [])

  const eliminar = async (id) => {
    if (!window.confirm('¿Eliminar este vehículo?')) return
    try {
      await apiFetch(`/api/vehiculos/${id}`, { method: 'DELETE' })
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="container">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Vehículos</h2>
        {isAdmin && (
          <Link to="/vehiculos/nuevo" className="btn btn-success">+ Nuevo vehículo</Link>
        )}
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      <table className="table table-striped table-hover">
        <thead>
          <tr>
            <th>ID</th>
            <th>Patente</th>
            <th>Marca</th>
            <th>Modelo</th>
            <th>Año</th>
            <th>Dueño</th>
            {isAdmin && <th>Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {vehiculos.map((v) => (
            <tr key={v.id}>
              <td>{v.id}</td>
              <td>{v.patente}</td>
              <td>{v.marca}</td>
              <td>{v.modelo}</td>
              <td>{v.anio}</td>
              <td>{v.clienteNombre}</td>
              {isAdmin && (
                <td>
                  <Link to={`/vehiculos/${v.id}/editar`} className="btn btn-sm btn-outline-secondary me-2">
                    Editar
                  </Link>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(v.id)}>
                    Eliminar
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
