"use client"

import { useState, useEffect } from "react"
import { Search } from "lucide-react"

import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import ProductCard from "@/components/product-card"
import type { Product, ProductsApiResponse } from "@/lib/types"

export default function ProductsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [category, setCategory] = useState("all")
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [categories, setCategories] = useState<{ value: string; label: string }[]>([
    { value: "all", label: "Todas las categorías" },
  ])

  // Función para cargar productos desde la API
  const fetchProducts = async (page = 0) => {
    setLoading(true)
    try {
      const response = await fetch(`http://localhost:8000/open/products?page=${page}&size=20`)

      if (!response.ok) {
        throw new Error("Error al cargar productos")
      }

      const data: ProductsApiResponse = await response.json()
      setProducts(data.data.content)
      setTotalPages(data.data.totalPages)

      // Extraer categorías únicas de los productos
      const uniqueCategories = new Map<number, string>()
      data.data.content.forEach((product) => {
        uniqueCategories.set(product.categoria.id, product.categoria.nombre)
      })

      // Actualizar lista de categorías
      const categoryOptions = [{ value: "all", label: "Todas las categorías" }]
      uniqueCategories.forEach((name, id) => {
        categoryOptions.push({ value: id.toString(), label: name })
      })
      setCategories(categoryOptions)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error desconocido")
      console.error("Error fetching products:", err)
    } finally {
      setLoading(false)
    }
  }

  // Cargar productos al montar el componente
  useEffect(() => {
    fetchProducts()
  }, [])

  // Cambiar de página
  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    fetchProducts(page)
  }

  // Filtrar productos por búsqueda y categoría
  const filteredProducts = products.filter((product) => {
    const matchesSearch = product.descripcion.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesCategory = category === "all" || product.categoria.id.toString() === category

    return matchesSearch && matchesCategory
  })

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Catálogo de Productos</h1>

      <div className="flex flex-col md:flex-row gap-4 mb-8">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Buscar productos..."
            className="pl-10"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger className="w-full md:w-[200px]">
            <SelectValue placeholder="Categoría" />
          </SelectTrigger>
          <SelectContent>
            {categories.map((category) => (
              <SelectItem key={category.value} value={category.value}>
                {category.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {loading ? (
        <div className="text-center py-12">
          <p>Cargando productos...</p>
        </div>
      ) : error ? (
        <div className="text-center py-12 text-destructive">
          <p>{error}</p>
          <Button variant="outline" className="mt-4" onClick={() => fetchProducts()}>
            Reintentar
          </Button>
        </div>
      ) : filteredProducts.length > 0 ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {filteredProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {totalPages > 1 && (
            <Pagination className="mt-8">
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    href="#"
                    onClick={(e) => {
                      e.preventDefault()
                      if (currentPage > 0) handlePageChange(currentPage - 1)
                    }}
                    className={currentPage === 0 ? "pointer-events-none opacity-50" : ""}
                  />
                </PaginationItem>

                {Array.from({ length: totalPages }).map((_, index) => {
                  // Mostrar solo algunas páginas para no sobrecargar la UI
                  if (
                    index === 0 ||
                    index === totalPages - 1 ||
                    (index >= currentPage - 1 && index <= currentPage + 1)
                  ) {
                    return (
                      <PaginationItem key={index}>
                        <PaginationLink
                          href="#"
                          onClick={(e) => {
                            e.preventDefault()
                            handlePageChange(index)
                          }}
                          isActive={currentPage === index}
                        >
                          {index + 1}
                        </PaginationLink>
                      </PaginationItem>
                    )
                  } else if (index === currentPage - 2 || index === currentPage + 2) {
                    return (
                      <PaginationItem key={index}>
                        <PaginationEllipsis />
                      </PaginationItem>
                    )
                  }
                  return null
                })}

                <PaginationItem>
                  <PaginationNext
                    href="#"
                    onClick={(e) => {
                      e.preventDefault()
                      if (currentPage < totalPages - 1) handlePageChange(currentPage + 1)
                    }}
                    className={currentPage === totalPages - 1 ? "pointer-events-none opacity-50" : ""}
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          )}
        </>
      ) : (
        <div className="text-center py-12">
          <h3 className="text-lg font-medium">No se encontraron productos</h3>
          <p className="text-muted-foreground mt-2">Intenta con otra búsqueda o categoría</p>
          <Button
            variant="outline"
            className="mt-4"
            onClick={() => {
              setSearchTerm("")
              setCategory("all")
            }}
          >
            Mostrar todos los productos
          </Button>
        </div>
      )}
    </div>
  )
}

