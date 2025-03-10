import { toast } from "@/hooks/use-toast"

interface ApiResponse<T> {
  message: string
  data: T
}

/**
 * Función para realizar solicitudes autenticadas a la API
 * Similar a un interceptor de solicitudes en Angular
 */
export async function fetchWithAuth<T>(url: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
  // Obtener el token del localStorage
  const token = localStorage.getItem("authToken")

  // Si no hay token y la URL no es pública, redirigir al login
  if (!token && !url.includes("/open/")) {
    toast({
      title: "Sesión expirada",
      description: "Por favor inicia sesión nuevamente",
      variant: "destructive",
    })

    // Redirigir al login después de un breve retraso
    setTimeout(() => {
      window.location.href = "/login"
    }, 1500)

    throw new Error("No autenticado")
  }

  // Configurar los headers con el token de autenticación
  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    })

    // Manejar errores de autenticación (401)
    if (response.status === 401) {
      // Limpiar el token expirado
      localStorage.removeItem("authToken")

      toast({
        title: "Sesión expirada",
        description: "Por favor inicia sesión nuevamente",
        variant: "destructive",
      })

      // Redirigir al login después de un breve retraso
      setTimeout(() => {
        window.location.href = "/login"
      }, 1500)

      throw new Error("Sesión expirada")
    }

    // Manejar otros errores
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || "Error en la solicitud")
    }

    // Parsear la respuesta
    const data: ApiResponse<T> = await response.json()
    return data
  } catch (error) {
    console.error("Error en la solicitud:", error)
    throw error
  }
}

/**
 * Verificar si el usuario está autenticado
 */
export function isAuthenticated(): boolean {
  return !!localStorage.getItem("authToken")
}

/**
 * Cerrar sesión
 */
export function logout(): void {
  localStorage.removeItem("authToken")
  window.location.href = "/login"
}

