import { useState } from 'react'
import { useNavigate, Navigate, Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

// Vista Login (rúbrica): formulario usuario/contraseña, consume /auth/login,
// muestra error si las credenciales son incorrectas y redirige al dashboard
// si ya hay sesión activa.
export default function Login() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  // Si ya hay sesión activa, no tiene sentido mostrar el login
  if (user) return <Navigate to="/" replace />

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(username, password)
      navigate('/')
    } catch (err) {
      // 401 del backend -> "Usuario o contraseña incorrectos"
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container" style={{ maxWidth: 420, marginTop: '10vh' }}>
      <div className="card shadow">
        <div className="card-body">
          <h3 className="card-title text-center mb-4">⚙️ Toño's Motors</h3>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Usuario</label>
              <input
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button className="btn btn-primary w-100 mb-3" disabled={loading}>
              {loading ? 'Ingresando...' : 'Ingresar'}
            </button>
            <div className="text-center">
              <span className="text-muted">¿No tienes cuenta?</span> <Link to="/register" className="text-warning text-decoration-none">Regístrate</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
