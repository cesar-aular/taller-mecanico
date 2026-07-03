import { useState, useEffect } from 'react'
import { apiFetch } from '../api/api.js'

export default function UserManagement() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchUsers()
  }, [])

  const fetchUsers = async () => {
    try {
      const data = await apiFetch('/api/users')
      setUsers(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const changeRole = async (username, newRole) => {
    if (!window.confirm(`¿Estás seguro de cambiar el rol de ${username} a ${newRole}?`)) return
    
    try {
      await apiFetch(`/api/users/${username}/role`, {
        method: 'PUT',
        body: JSON.stringify({ rol: newRole })
      })
      alert("Rol actualizado exitosamente")
      fetchUsers()
    } catch (err) {
      alert("Error al actualizar rol: " + err.message)
    }
  }

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-warning" /></div>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <div className="container">
      <h2 className="mb-4 text-warning">Gestión de Usuarios</h2>
      <p className="text-light">Panel de administración para visualizar a todos los usuarios registrados y modificar sus privilegios de acceso.</p>
      
      <div className="card shadow mt-4">
        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-dark table-hover table-striped mb-0">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Correo Electrónico</th>
                  <th>Rol Actual</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.username}>
                    <td><strong>{u.username}</strong></td>
                    <td>{u.email}</td>
                    <td>
                      <span className={`badge ${u.roles.includes('ROLE_ADMIN') ? 'bg-warning text-dark' : 'bg-info text-dark'}`}>
                        {u.roles.map(r => r.replace('ROLE_', '')).join(', ')}
                      </span>
                    </td>
                    <td>
                      {u.roles.includes('ROLE_ADMIN') ? (
                        <button className="btn btn-sm btn-outline-info" onClick={() => changeRole(u.username, 'ROLE_USER')}>
                          Hacer Usuario Normal
                        </button>
                      ) : (
                        <button className="btn btn-sm btn-outline-warning" onClick={() => changeRole(u.username, 'ROLE_ADMIN')}>
                          Hacer Administrador
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {users.length === 0 && (
                  <tr><td colSpan="4" className="text-center">No hay usuarios registrados.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
