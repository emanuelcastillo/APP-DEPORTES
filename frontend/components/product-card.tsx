"use client"

import { useState } from "react"
import Image from "next/image"
import { ShoppingCart } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { toast } from "@/hooks/use-toast"
import type { Product } from "@/lib/types"
import { fetchWithAuth } from "@/lib/auth"

interface ProductCardProps {
  product: Product
}

export default function ProductCard({ product }: ProductCardProps) {
  const [isAddingToCart, setIsAddingToCart] = useState(false)

  const handleAddToCart = async () => {
    setIsAddingToCart(true)

    try {
      await fetchWithAuth(`http://localhost:8000/shopping-cart/add-product/${product.id}?quantity=1`, {
        method: "POST",
      })

      // Producto añadido exitosamente
      toast({
        title: "Producto añadido",
        description: `${product.descripcion} ha sido añadido a tu carrito`,
      })

      // Actualizar el contador del carrito
      window.dispatchEvent(new CustomEvent("cart-updated"))
    } catch (error) {
      console.error("Error adding product to cart:", error)

      // El manejo de errores de autenticación ya está en fetchWithAuth
      if (error instanceof Error && error.message !== "No autenticado" && error.message !== "Sesión expirada") {
        toast({
          title: "Error",
          description: error.message || "Error al añadir el producto al carrito",
          variant: "destructive",
        })
      }
    } finally {
      setIsAddingToCart(false)
    }
  }

  return (
    <Card className="overflow-hidden">
      <div className="relative aspect-square">
        <Image
          src={product.rutaImagen || "/placeholder.svg"}
          alt={product.descripcion}
          fill
          className="object-cover transition-transform hover:scale-105"
        />
      </div>
      <CardContent className="p-4">
        <div className="space-y-1">
          <h3 className="font-medium truncate">{product.descripcion}</h3>
          <p className="text-sm text-muted-foreground truncate">Categoría: {product.categoria.nombre}</p>
        </div>
        <div className="flex items-center justify-between mt-2">
          <span className="font-bold">Q{product.monto.toFixed(2)}</span>
          <span className="text-sm text-muted-foreground">
            {product.cantidadDisponible > 0 ? `${product.cantidadDisponible} disponibles` : "Agotado"}
          </span>
        </div>
      </CardContent>
      <CardFooter className="p-4 pt-0">
        <Button
          className="w-full"
          disabled={product.cantidadDisponible === 0 || isAddingToCart}
          onClick={handleAddToCart}
        >
          <ShoppingCart className="h-4 w-4 mr-2" />
          {isAddingToCart ? "Añadiendo..." : "Añadir al carrito"}
        </Button>
      </CardFooter>
    </Card>
  )
}

