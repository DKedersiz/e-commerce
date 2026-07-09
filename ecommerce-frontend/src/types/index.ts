export interface AuthResponse {
  accessToken: string
  refreshToken: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface OrderItem {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
}

export interface Order {
  id: number
  orderStatus: "PENDING" | "COMPLETED" | "FAILED"
  totalAmount: number
  createdAt: string
  items: OrderItem[]
}

export interface Category {
  id: number
  name: string
  slug: string
  description: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  categoryName?: string
  categorySlug?: string
  averageRating?: number
  reviewCount?: number
}

export interface AiChatRequest {
  message: string
}

export interface AiChatResponse {
  message: string
  recommendedProductIds: number[]
}

export interface ReviewResponse {
  id: number
  productId: number
  rating: number
  comment: string
  userName: string
  createdAt: string
}

export interface ReviewCreateRequest {
  productId: number
  rating: number
  comment: string
}
