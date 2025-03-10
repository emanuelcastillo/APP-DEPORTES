"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { CheckCircle } from "lucide-react"

import { Button } from "@/components/ui/button"
import type { OrderResponse } from "@/lib/types"
import { isAuthenticated } from "@/lib/auth"

export default function OrderConfirmationPage() {
  const router = useRouter()
  const [orderData, setOrderData] = useState<OrderResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
      return
    }
  
    try {
      const orderDataStr = sessionStorage.getItem("lastOrder")
      console.log("Retrieved from sessionStorage:", orderDataStr)
  
      if (orderDataStr) {
        const parsedOrder = JSON.parse(orderDataStr) as OrderResponse
        console.log("Parsed order data:", parsedOrder)
        setOrderData(parsedOrder)
  
        // Eliminar los datos cuando el usuario salga de la página
        const handleUnload = () => sessionStorage.removeItem("lastOrder")
        window.addEventListener("beforeunload", handleUnload)
  
        return () => window.removeEventListener("beforeunload", handleUnload)
      } else {
        setError("No se encontró información del pedido")
        console.error("No order data found in sessionStorage")
      }
    } catch (error) {
      console.error("Error retrieving order data:", error)
      setError("Error al cargar la información del pedido")
    } finally {
      setIsLoading(false)
    }
  }, [])

  // Formatear la fecha de la orden
  const formatOrderDate = (dateString: string) => {
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString() + " " + date.toLocaleTimeString()
    } catch (error) {
      return dateString
    }
  }

  // Mostrar estado de carga
  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-md mx-auto text-center">
          <p>Cargando información del pedido...</p>
        </div>
      </div>
    )
  }

  // Mostrar error si ocurrió alguno
  if (error || !orderData) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-md mx-auto text-center space-y-6">
          <h1 className="text-2xl font-bold text-destructive">Error</h1>
          <p>{error || "No se pudo cargar la información del pedido"}</p>
          <Button asChild>
            <Link href="/products">Volver a la tienda</Link>
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-md mx-auto text-center space-y-6">
        <CheckCircle className="mx-auto h-16 w-16 text-primary" />

        <div className="space-y-2">
          <h1 className="text-3xl font-bold">¡Pedido Confirmado!</h1>
          <p className="text-muted-foreground">
            Gracias por tu compra. Tu pedido ha sido recibido y está siendo procesado.
          </p>
        </div>

        <div className="bg-muted p-6 rounded-lg text-left space-y-4">
          <div>
            <p className="text-sm text-muted-foreground">Número de pedido</p>
            <p className="font-medium">{orderData.numeroOrden}</p>
          </div>

          <div>
            <p className="text-sm text-muted-foreground">Fecha</p>
            <p className="font-medium">{formatOrderDate(orderData.fechaCreacion)}</p>
          </div>

          <div>
            <p className="text-sm text-muted-foreground">Total</p>
            <p className="font-medium">Q{orderData.total.toFixed(2)}</p>
          </div>

          <div>
            <p className="text-sm text-muted-foreground">Estado</p>
            <p className="font-medium">{orderData.estado}</p>
          </div>

          <div>
            <p className="text-sm text-muted-foreground">Dirección de envío</p>
            <p className="font-medium">{orderData.direccionEnvio}</p>
          </div>
        </div>

        <p className="text-sm text-muted-foreground">
          Recibirás un correo electrónico con los detalles de tu pedido y actualizaciones sobre el estado de envío.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button asChild>
            <Link href="/profile?tab=orders">Ver mis pedidos</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/products">Seguir comprando</Link>
          </Button>
        </div>
      </div>
    </div>
  )
}

