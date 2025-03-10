"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import Image from "next/image"
import { useRouter } from "next/navigation"
import { Trash2, ShoppingBag } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Separator } from "@/components/ui/separator"
import { toast } from "@/hooks/use-toast"
import type { CartItem, OrderResponse } from "@/lib/types"
import { fetchWithAuth, isAuthenticated } from "@/lib/auth"

export default function CartPage() {
  const router = useRouter()
  const [cartItems, setCartItems] = useState<CartItem[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isUpdating, setIsUpdating] = useState(false)
  const [isCheckingOut, setIsCheckingOut] = useState(false)
  const [total, setTotal] = useState(0)
  const [error, setError] = useState<string | null>(null)

  // Función para obtener los items del carrito
  const fetchCartItems = async () => {
    try {
      // Verificar si el usuario está autenticado
      if (!isAuthenticated()) {
        router.push("/login")
        return
      }

      const response = await fetchWithAuth<CartItem[]>("http://localhost:8000/shopping-cart/items", {
        method: "GET",
      })

      setCartItems(response.data)

      // Obtener el total del carrito
      await fetchCartTotal()
    } catch (error) {
      console.error("Error fetching cart items:", error)
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        setError(error.message || "Error desconocido")
      }
    } finally {
      setIsLoading(false)
    }
  }

  // Función para obtener el total del carrito
  const fetchCartTotal = async () => {
    try {
      const response = await fetchWithAuth<number>("http://localhost:8000/shopping-cart/total", {
        method: "POST",
      })

      setTotal(response.data)
    } catch (error) {
      console.error("Error fetching cart total:", error)
    }
  }

  // Función para actualizar la cantidad de un producto
  const updateQuantity = async (productId: number, quantity: number) => {
    if (quantity < 1) return

    setIsUpdating(true)

    try {
      await fetchWithAuth(
        `http://localhost:8000/shopping-cart/update-product-quantity/${productId}?quantity=${quantity}`,
        {
          method: "POST",
        },
      )

      // Actualizar la UI
      setCartItems((prevItems) =>
        prevItems.map((item) => (item.producto.id === productId ? { ...item, cantidad: quantity } : item)),
      )

      // Actualizar el total
      await fetchCartTotal()

      // Notificar al header que el carrito ha cambiado
      window.dispatchEvent(new CustomEvent("cart-updated"))
    } catch (error) {
      console.error("Error updating quantity:", error)
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        toast({
          title: "Error",
          description: error.message || "Error al actualizar la cantidad",
          variant: "destructive",
        })
      }
    } finally {
      setIsUpdating(false)
    }
  }

  // Función para eliminar un producto del carrito
  const removeItem = async (productId: number) => {
    setIsUpdating(true)

    try {
      await fetchWithAuth(`http://localhost:8000/shopping-cart/remove-product/${productId}`, {
        method: "DELETE",
      })

      // Actualizar la UI
      setCartItems((prevItems) => prevItems.filter((item) => item.producto.id !== productId))

      // Actualizar el total
      await fetchCartTotal()

      // Notificar al header que el carrito ha cambiado
      window.dispatchEvent(new CustomEvent("cart-updated"))

      toast({
        title: "Producto eliminado",
        description: "El producto ha sido eliminado del carrito",
      })
    } catch (error) {
      console.error("Error removing item:", error)
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        toast({
          title: "Error",
          description: error.message || "Error al eliminar el producto",
          variant: "destructive",
        })
      }
    } finally {
      setIsUpdating(false)
    }
  }

  // Función para vaciar el carrito
  const emptyCart = async () => {
    if (!confirm("¿Estás seguro de que deseas vaciar el carrito?")) return

    setIsUpdating(true)

    try {
      await fetchWithAuth("http://localhost:8000/shopping-cart/empty-cart", {
        method: "POST",
      })

      // Actualizar la UI
      setCartItems([])
      setTotal(0)

      // Notificar al header que el carrito ha cambiado
      window.dispatchEvent(new CustomEvent("cart-updated"))

      toast({
        title: "Carrito vaciado",
        description: "Todos los productos han sido eliminados del carrito",
      })
    } catch (error) {
      console.error("Error emptying cart:", error)
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        toast({
          title: "Error",
          description: error.message || "Error al vaciar el carrito",
          variant: "destructive",
        })
      }
    } finally {
      setIsUpdating(false)
    }
  }

  // Función para procesar el checkout
  const handleCheckout = async () => {
    setIsCheckingOut(true)

    try {
      const response = await fetchWithAuth<OrderResponse>("http://localhost:8000/shopping-cart/checkout", {
        method: "POST",
      })

      console.log("Checkout response:", response) // Para depuración

      // Notificar al header que el carrito ha cambiado
      window.dispatchEvent(new CustomEvent("cart-updated"))

      // Guardar los datos de la orden en sessionStorage (más seguro que localStorage para datos temporales)
      if (response && response.data) {
        sessionStorage.setItem("lastOrder", JSON.stringify(response.data))
        console.log("Order data saved to sessionStorage") // Para depuración
      } else {
        console.error("Invalid checkout response:", response)
        throw new Error("Respuesta de checkout inválida")
      }

      // Redirigir a la página de confirmación después de un breve retraso
      // para asegurar que los datos se guarden correctamente
      setTimeout(() => {
        router.push("/order-confirmation")
      }, 100)
    } catch (error) {
      console.error("Error during checkout:", error)
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        toast({
          title: "Error",
          description: error.message || "Error al procesar el pedido",
          variant: "destructive",
        })
      }
      setIsCheckingOut(false)
    }
  }

  // Cargar los items del carrito al montar el componente
  useEffect(() => {
    fetchCartItems()
  }, [])

  // Calcular impuestos (16%)
  const tax = total * 0.16

  // Total con impuestos
  const totalWithTax = total + tax

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Carrito de Compras</h1>
        <div className="text-center py-12">
          <p>Cargando carrito...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Carrito de Compras</h1>
        <div className="text-center py-12 text-destructive">
          <p>{error}</p>
          <Button
            variant="outline"
            className="mt-4"
            onClick={() => {
              setError(null)
              setIsLoading(true)
              fetchCartItems()
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
      <h1 className="text-3xl font-bold mb-6">Carrito de Compras</h1>

      {cartItems.length === 0 ? (
        <div className="text-center py-12">
          <ShoppingBag className="mx-auto h-12 w-12 text-muted-foreground" />
          <h2 className="mt-4 text-lg font-medium">Tu carrito está vacío</h2>
          <p className="mt-2 text-muted-foreground">Parece que no has añadido ningún producto a tu carrito todavía.</p>
          <Button asChild className="mt-6">
            <Link href="/products">Explorar productos</Link>
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-4">
            {cartItems.map((item) => (
              <Card key={item.id}>
                <CardContent className="p-4">
                  <div className="flex items-center gap-4">
                    <div className="relative h-20 w-20 rounded-md overflow-hidden">
                      <Image
                        src={item.producto.rutaImagen || "/placeholder.svg"}
                        alt={item.producto.descripcion}
                        fill
                        className="object-cover"
                      />
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-medium truncate">{item.producto.descripcion}</h3>
                      <p className="text-sm text-muted-foreground truncate">
                        Categoría: {item.producto.categoria.nombre}
                      </p>
                      <p className="text-sm font-medium mt-1">Q{item.precioUnitario.toFixed(2)}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="flex items-center">
                        <Button
                          variant="outline"
                          size="icon"
                          className="h-8 w-8 rounded-r-none"
                          onClick={() => updateQuantity(item.producto.id, item.cantidad - 1)}
                          disabled={isUpdating || item.cantidad <= 1}
                        >
                          -
                        </Button>
                        <Input
                          type="number"
                          value={item.cantidad}
                          onChange={(e) => updateQuantity(item.producto.id, Number.parseInt(e.target.value) || 1)}
                          className="h-8 w-12 rounded-none text-center [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
                          disabled={isUpdating}
                        />
                        <Button
                          variant="outline"
                          size="icon"
                          className="h-8 w-8 rounded-l-none"
                          onClick={() => updateQuantity(item.producto.id, item.cantidad + 1)}
                          disabled={isUpdating || item.cantidad >= item.producto.cantidadDisponible}
                        >
                          +
                        </Button>
                      </div>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 text-destructive"
                        onClick={() => removeItem(item.producto.id)}
                        disabled={isUpdating}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}

            <div className="flex justify-end">
              <Button variant="outline" className="text-destructive" onClick={emptyCart} disabled={isUpdating}>
                Vaciar carrito
              </Button>
            </div>
          </div>

          <div>
            <Card>
              <CardHeader>
                <CardTitle>Resumen del pedido</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span>Q{total.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Impuestos (16%)</span>
                  <span>Q{tax.toFixed(2)}</span>
                </div>
                <Separator />
                <div className="flex justify-between font-bold">
                  <span>Total</span>
                  <span>Q{totalWithTax.toFixed(2)}</span>
                </div>
              </CardContent>
              <CardFooter>
                <Button className="w-full" onClick={handleCheckout} disabled={isCheckingOut || isUpdating}>
                  {isCheckingOut ? "Procesando..." : "Proceder al pago"}
                </Button>
              </CardFooter>
            </Card>
            <div className="mt-4 text-center">
              <Link href="/products" className="text-sm text-primary hover:underline">
                Continuar comprando
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

