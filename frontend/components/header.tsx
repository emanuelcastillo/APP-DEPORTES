"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { Menu, ShoppingCart, User, LogOut } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { cn } from "@/lib/utils"
import { fetchWithAuth, isAuthenticated, logout } from "@/lib/auth"

const routes = [
  {
    href: "/",
    label: "Inicio",
  },
  {
    href: "/products",
    label: "Productos",
  },
]

export default function Header() {
  const [isOpen, setIsOpen] = useState(false)
  const pathname = usePathname()

  // Estado de autenticación
  const [isAuth, setIsAuth] = useState(false)

  // Estado para el contador del carrito
  const [cartItemsCount, setCartItemsCount] = useState(0)

  // Función para obtener el número de items en el carrito
  const fetchCartCount = async () => {
    if (!isAuth) return

    try {
      const response = await fetchWithAuth<number>("http://localhost:8000/shopping-cart/count", {
        method: "POST",
      })

      setCartItemsCount(response.data)
    } catch (error) {
      console.error("Error fetching cart count:", error)
    }
  }

  // Comprobar si hay un token de autenticación al cargar el componente
  useEffect(() => {
    const auth = isAuthenticated()
    setIsAuth(auth)

    if (auth) {
      fetchCartCount()
    }

    // Escuchar eventos de actualización del carrito
    const handleCartUpdated = () => {
      fetchCartCount()
    }

    window.addEventListener("cart-updated", handleCartUpdated)

    return () => {
      window.removeEventListener("cart-updated", handleCartUpdated)
    }
  }, [])

  // Función para cerrar sesión
  const handleLogout = () => {
    logout()
    setIsAuth(false)
    setCartItemsCount(0)
  }

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        <div className="flex items-center gap-6 md:gap-10">
          <Link href="/" className="font-bold text-xl">
            Deportes Elite
          </Link>

          <nav className="hidden md:flex gap-6">
            {routes.map((route) => (
              <Link
                key={route.href}
                href={route.href}
                className={cn(
                  "text-sm font-medium transition-colors hover:text-primary",
                  pathname === route.href ? "text-primary" : "text-muted-foreground",
                )}
              >
                {route.label}
              </Link>
            ))}
          </nav>
        </div>

        <div className="flex items-center gap-4">
          <Link href="/cart" className="relative">
            <Button variant="ghost" size="icon" className="relative">
              <ShoppingCart className="h-5 w-5" />
              {cartItemsCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-primary text-primary-foreground text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                  {cartItemsCount}
                </span>
              )}
            </Button>
          </Link>

          {isAuth ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <User className="h-5 w-5" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>Mi cuenta</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link href="/profile">Perfil</Link>
                </DropdownMenuItem>
                <DropdownMenuItem asChild>
                  <Link href="/profile?tab=orders">Mis pedidos</Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleLogout} className="text-destructive">
                  <LogOut className="h-4 w-4 mr-2" />
                  Cerrar sesión
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Button variant="outline" size="sm" asChild className="hidden md:inline-flex">
              <Link href="/login">Iniciar Sesión</Link>
            </Button>
          )}

          <Sheet open={isOpen} onOpenChange={setIsOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon" className="md:hidden">
                <Menu className="h-5 w-5" />
                <span className="sr-only">Toggle menu</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="left">
              <div className="flex flex-col space-y-6 mt-6">
                <Link href="/" className="font-bold text-xl" onClick={() => setIsOpen(false)}>
                  Deportes Elite
                </Link>
                <nav className="flex flex-col space-y-4">
                  {routes.map((route) => (
                    <Link
                      key={route.href}
                      href={route.href}
                      className={cn(
                        "text-sm font-medium transition-colors hover:text-primary",
                        pathname === route.href ? "text-primary" : "text-muted-foreground",
                      )}
                      onClick={() => setIsOpen(false)}
                    >
                      {route.label}
                    </Link>
                  ))}
                  {!isAuth ? (
                    <Link
                      href="/login"
                      className="text-sm font-medium transition-colors hover:text-primary"
                      onClick={() => setIsOpen(false)}
                    >
                      Iniciar Sesión
                    </Link>
                  ) : (
                    <button
                      className="text-sm font-medium text-destructive hover:text-destructive/80 text-left flex items-center"
                      onClick={() => {
                        handleLogout()
                        setIsOpen(false)
                      }}
                    >
                      <LogOut className="h-4 w-4 mr-2" />
                      Cerrar sesión
                    </button>
                  )}
                </nav>
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  )
}

