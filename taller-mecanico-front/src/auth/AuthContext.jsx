import { createContext, useContext, useState } from 'react'
import { apiFetch, saveToken, clearToken, getToken, parseJwt } from '../api/api.js'

// Contexto de sesión: cualquier componente puede saber quién está logueado
// y qué roles tiene, sin pasar props de mano en mano.
const AuthContext = createContext(null)

// Convierte el token guardado en un objeto usuario {username, roles[]}.
// Si el token expiró (claim exp en segundos), se descarta.
function userFromToken() {
  const token = getToken()
  if (!token) return null
  const payload = parseJwt(token)
  if (!payload || payload.exp * 1000 < Date.now()) {
    clearToken()
    return null
  }
  return {
    username: payload.sub,
    roles: (payload.roles || '').split(','),
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(userFromToken)

  // Consume POST /auth/login; si es exitoso guarda el token en localStorage.
  const login = async (username, password) => {
    const data = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    })
    saveToken(data.token)
    setUser(userFromToken())
  }

  // Cerrar sesión: elimina el token; las rutas protegidas dejan de ser accesibles.
  const logout = () => {
    clearToken()
    setUser(null)
  }

  const isAdmin = Boolean(user?.roles.includes('ROLE_ADMIN'))

  return (
    <AuthContext.Provider value={{ user, isAdmin, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
