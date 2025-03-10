"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { User, Package, LogOut } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { toast } from "@/hooks/use-toast"
import { fetchWithAuth, logout, isAuthenticated } from "@/lib/auth"

// Interfaz para los datos del usuario
interface UserData {
  id: number
  nombre: string
  apellido: string
  direccionEnvio: string
  email: string
  fechaNacimiento: string
  username: string
}

// Interfaz para el pedido
interface Order {
  id: number
  numeroOrden: string
  fechaCreacion: string
  total: number
  direccionEnvio: string
  estado: string
}

// Validación de formulario con Zod
const formSchema = z.object({
  firstName: z.string().min(2, {
    message: "El nombre debe tener al menos 2 caracteres.",
  }),
  lastName: z.string().min(2, {
    message: "El apellido debe tener al menos 2 caracteres.",
  }),
  email: z.string().email({
    message: "Por favor ingresa un correo electrónico válido.",
  }),
  address: z.string().min(10, {
    message: "La dirección debe tener al menos 10 caracteres.",
  }),
})

export default function ProfilePage() {
  const router = useRouter()
  const [orders, setOrders] = useState<Order[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [userData, setUserData] = useState<UserData | null>(null)
  const [isLoadingUser, setIsLoadingUser] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Verificar autenticación
  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
      return
    }

    // Cargar datos del usuario
    fetchUserData()
    fetchOrders()
  }, [])

  // Función para obtener los datos del usuario
  const fetchUserData = async () => {
    try {
      const response = await fetchWithAuth<UserData>("http://localhost:8000/users/me")
      setUserData(response.data)

      // Actualizar el formulario con los datos del usuario
      form.reset({
        firstName: response.data.nombre,
        lastName: response.data.apellido,
        email: response.data.email,
        address: response.data.direccionEnvio,
      })
    } catch (error) {
      console.error("Error fetching user data:", error)
      setError("No se pudieron cargar los datos del usuario")
    } finally {
      setIsLoadingUser(false)
    }
  }

  const fetchOrders = async () => {
    try {
      const response = await fetchWithAuth<{ data: { content: Order[] } }>("http://localhost:8000/users/me/orders")
      setOrders(response.data.content)
    } catch (error) {
      console.error("Error fetching orders:", error)
      setError("No se pudieron cargar las órdenes")
    } finally {
      setIsLoading(false)
    }
  }

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      address: "",
    },
  })

  // Modificar la función onSubmit para enviar los datos actualizados a la API
  function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true)

    // Crear el objeto con el formato esperado por la API
    const updateData = {
      nombre: values.firstName,
      apellido: values.lastName,
      email: values.email,
      direccionEnvio: values.address,
    }

    // Enviar la solicitud PATCH a la API
    fetchWithAuth<UserData>("http://localhost:8000/users/me", {
      method: "PATCH",
      body: JSON.stringify(updateData),
    })
      .then((response) => {
        // Actualizar los datos del usuario en el estado
        setUserData(response.data)

        toast({
          title: "Perfil actualizado",
          description: "Tu información ha sido actualizada correctamente.",
        })
      })
      .catch((error) => {
        console.error("Error updating profile:", error)
        toast({
          title: "Error al actualizar",
          description: error instanceof Error ? error.message : "No se pudo actualizar tu información",
          variant: "destructive",
        })
      })
      .finally(() => {
        setIsLoading(false)
      })
  }

  // Función para manejar el cierre de sesión
  const handleLogout = () => {
    logout()
  }

  if (isLoadingUser) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Mi Cuenta</h1>
        <div className="text-center py-12">
          <p>Cargando datos del usuario...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Mi Cuenta</h1>
        <div className="text-center py-12 text-destructive">
          <p>{error}</p>
          <Button
            variant="outline"
            className="mt-4"
            onClick={() => {
              setError(null)
              setIsLoadingUser(true)
              fetchUserData()
            }}
          >
            Reintentar
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Mi Cuenta</h1>

      <Tabs defaultValue="profile" className="space-y-6">
        <TabsList className="grid w-full grid-cols-2 md:w-auto md:inline-flex">
          <TabsTrigger value="profile" className="flex items-center gap-2">
            <User className="h-4 w-4" />
            <span>Perfil</span>
          </TabsTrigger>
          <TabsTrigger value="orders" className="flex items-center gap-2">
            <Package className="h-4 w-4" />
            <span>Mis Pedidos</span>
          </TabsTrigger>
        </TabsList>

        <TabsContent value="profile" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Información Personal</CardTitle>
              <CardDescription>Actualiza tu información personal y dirección de envío.</CardDescription>
            </CardHeader>
            <CardContent>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="firstName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Nombre</FormLabel>
                          <FormControl>
                            <Input {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="lastName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Apellido</FormLabel>
                          <FormControl>
                            <Input {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Correo electrónico</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="address"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Dirección de envío</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <div className="flex justify-end">
                    <Button type="submit" disabled={isLoading}>
                      {isLoading ? "Guardando..." : "Guardar cambios"}
                    </Button>
                  </div>
                </form>
              </Form>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Seguridad</CardTitle>
              <CardDescription>Gestiona la seguridad de tu cuenta.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center">
                <div>
                  <h3 className="font-medium">Cerrar sesión</h3>
                  <p className="text-sm text-muted-foreground">Cierra tu sesión en todos los dispositivos.</p>
                </div>
                <Button variant="destructive" className="flex items-center gap-2" onClick={handleLogout}>
                  <LogOut className="h-4 w-4" />
                  <span>Cerrar sesión</span>
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="orders" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Historial de Pedidos</CardTitle>
              <CardDescription>Consulta el estado y detalles de tus pedidos anteriores.</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <p className="text-center">Cargando órdenes...</p>
              ) : error ? (
                <div className="text-center text-destructive">
                  <p>{error}</p>
                  <Button
                    variant="outline"
                    className="mt-4"
                    onClick={() => {
                      setError(null)
                      setIsLoading(true)
                      fetchOrders()
                    }}
                  >
                    Reintentar
                  </Button>
                </div>
              ) : orders.length > 0 ? (
                <div className="space-y-6">
                  {orders.map((order) => (
                    <div key={order.id} className="border rounded-lg overflow-hidden">
                      <div className="bg-muted p-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
                        <div>
                          <div className="flex items-center gap-2">
                            <h3 className="font-medium">Pedido #{order.numeroOrden}</h3>
                            <span className="text-xs bg-primary/10 text-primary px-2 py-1 rounded-full">
                              {order.estado}
                            </span>
                          </div>
                          <p className="text-sm text-muted-foreground">Fecha: {new Date(order.fechaCreacion).toLocaleDateString()}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-medium">Q{order.total.toFixed(2)}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-6">
                  <Package className="mx-auto h-12 w-12 text-muted-foreground" />
                  <h3 className="mt-4 text-lg font-medium">No tienes pedidos</h3>
                  <p className="mt-2 text-muted-foreground">Cuando realices un pedido, aparecerá aquí.</p>
                  <Button asChild className="mt-4">
                    <Link href="/products">Explorar productos</Link>
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}

