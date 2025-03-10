export interface Category {
  id: number
  nombre: string
}

export interface Product {
  id: number
  descripcion: string
  monto: number
  cantidadDisponible: number
  rutaImagen: string
  categoria: Category
}

export interface PaginatedResponse<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      empty: boolean
      sorted: boolean
      unsorted: boolean
    }
    offset: number
    paged: boolean
    unpaged: boolean
  }
  last: boolean
  totalElements: number
  totalPages: number
  size: number
  number: number
  sort: {
    empty: boolean
    sorted: boolean
    unsorted: boolean
  }
  numberOfElements: number
  first: boolean
  empty: boolean
}

export interface ProductsApiResponse {
  message: string
  data: PaginatedResponse<Product>
}

export interface User {
  id: string
  firstName: string
  lastName: string
  email: string
  address: string
  birthDate: Date
}

export interface CartItem {
  id: number
  producto: Product
  cantidad: number
  precioUnitario: number
}

export interface CartResponse {
  message: string
  data: CartItem[]
}

export interface CartCountResponse {
  message: string
  data: number
}

export interface CartTotalResponse {
  message: string
  data: number
}

export interface Order {
  id: string
  userId: string
  items: OrderItem[]
  total: number
  status: "pending" | "processing" | "shipped" | "delivered" | "cancelled"
  shippingAddress: string
  createdAt: Date
}

export interface OrderItem {
  productId: number
  name: string
  price: number
  quantity: number
}

// Añadir la interfaz para la respuesta del checkout
export interface OrderResponse {
  id: number
  numeroOrden: string
  fechaCreacion: string
  total: number
  direccionEnvio: string
  estado: string
  items: OrderItemResponse[]
}


export interface OrderItemResponse {
  id: number
  cantidad: number
  precioUnitario: number
  producto: Product
}

