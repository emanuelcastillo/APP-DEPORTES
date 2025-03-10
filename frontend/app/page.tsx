import Link from "next/link"
import { ShoppingCart } from "lucide-react"

import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <div className="container mx-auto px-4 py-8">
      <section className="mb-12">
        <div className="flex flex-col items-center text-center space-y-4 mb-8">
          <h1 className="text-4xl font-bold tracking-tight">Deportes Elite</h1>
          <p className="text-muted-foreground max-w-[700px]">
            Tu tienda de confianza para artículos deportivos de alta calidad. Encuentra todo lo que necesitas para tu
            deporte favorito.
          </p>
          <div className="flex gap-4">
            <Button asChild>
              <Link href="/products">Ver Catálogo</Link>
            </Button>
            <Button variant="outline" asChild>
              <Link href="/register">Registrarse</Link>
            </Button>
          </div>
        </div>
      </section>

      <section className="mb-12">
        <div className="bg-muted rounded-lg p-6 md:p-8">
          <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            <div className="space-y-4 text-center md:text-left">
              <h2 className="text-2xl font-bold">¿Ya tienes una cuenta?</h2>
              <p className="text-muted-foreground max-w-[500px]">
                Inicia sesión para gestionar tus compras, ver tu historial de pedidos y actualizar tu información.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center md:justify-start">
                <Button asChild>
                  <Link href="/login">Iniciar Sesión</Link>
                </Button>
                <Button variant="outline" asChild>
                  <Link href="/register">Crear Cuenta</Link>
                </Button>
              </div>
            </div>
            <div className="bg-background p-6 rounded-lg shadow-sm">
              <div className="flex items-center gap-4 text-center">
                <ShoppingCart className="h-12 w-12 text-primary" />
                <div>
                  <h3 className="font-medium">Carrito de Compras</h3>
                  <p className="text-sm text-muted-foreground">Gestiona tus artículos fácilmente</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

