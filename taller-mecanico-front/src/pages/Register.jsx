import { useState } from 'react'
import { useNavigate, Link, Navigate } from 'react-router-dom'
import { apiFetch } from '../api/api.js'
import { useAuth } from '../auth/AuthContext.jsx'

export default function Register() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [formData, setFormData] = useState({ username: '', email: '', password: '', rol: 'ROLE_USER' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  if (user) return <Navigate to="/" replace />

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    // Validaciones basicas frontend
    if (formData.username.length < 4) {
      setError("El usuario debe tener al menos 4 caracteres")
      setLoading(false)
      return
    }
    if (formData.password.length < 6) {
      setError("La contraseña debe tener al menos 6 caracteres")
      setLoading(false)
      return
    }

    try {
      await apiFetch('/auth/register', {
        method: 'POST',
        body: JSON.stringify(formData)
      })
      setSuccess("¡Registro exitoso! Ya puedes iniciar sesión.")
      setTimeout(() => navigate('/login'), 2000)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 450, marginTop: '5vh' }}>
      <div className="card shadow border-warning">
        <div className="card-body">
          <h3 className="card-title text-center mb-4 text-warning">Registro de Usuario</h3>
          {error && <div className="alert alert-danger">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}
          
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label text-light">Usuario</label>
              <input
                type="text"
                name="username"
                className="form-control bg-dark text-light"
                value={formData.username}
                onChange={handleChange}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label text-light">Correo Electrónico</label>
              <input
                type="email"
                name="email"
                className="form-control bg-dark text-light"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label text-light">Contraseña</label>
              <input
                type="password"
                name="password"
                className="form-control bg-dark text-light"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>
            
            <button className="btn btn-warning w-100 fw-bold mb-3" disabled={loading}>
              {loading ? 'Registrando...' : 'Crear Cuenta'}
            </button>
            <div className="text-center">
              <span className="text-muted">¿Ya tienes cuenta?</span> <Link to="/login" className="text-info text-decoration-none">Inicia sesión</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
