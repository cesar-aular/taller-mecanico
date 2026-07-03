// Capa de acceso a la API: TODAS las peticiones HTTP pasan por aquí.
// Centraliza: la URL base, el header Authorization y el manejo del 401.

const BASE_URL = 'http://localhost:8080'

export function getToken() {
  return localStorage.getItem('token')
}

export function saveToken(token) {
  localStorage.setItem('token', token)
}

export function clearToken() {
  localStorage.removeItem('token')
}

// Decodifica el payload del JWT (la parte central del token, en Base64URL)
// para leer los claims: sub (username), roles, exp.
export function parseJwt(token) {
  try {
    const payload = token.split('.')[1]
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch {
    return null
  }
}

// Wrapper de fetch usado por toda la app.
export async function apiFetch(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers }

  // Si hay sesión, el token viaja en el header Authorization: Bearer <token>
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(BASE_URL + path, { ...options, headers })

  // Regla de la rúbrica: si el servidor responde 401 en un endpoint protegido,
  // se limpia el token y se redirige automáticamente al login.
  // (No aplica al login mismo: ahí el 401 significa "credenciales incorrectas").
  if (response.status === 401 && !path.startsWith('/auth')) {
    clearToken()
    window.location.href = '/login'
    throw new Error('Sesión expirada')
  }

  // 204 No Content (DELETE exitoso) no trae cuerpo JSON
  if (response.status === 204) return null

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    // El backend responde ErrorInfo {message, statusCode, uriRequested}
    // o, en errores de validación, un mapa {campo: mensaje}.
    const message = data?.message
      ? data.message
      : data
        ? Object.values(data).join('. ')
        : `Error HTTP ${response.status}`
    const error = new Error(message)
    error.status = response.status
    error.fields = data && !data.message ? data : null
    throw error
  }

  return data
}
