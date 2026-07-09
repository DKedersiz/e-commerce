import { create } from "zustand"
import { persist } from "zustand/middleware"
import { Product } from "@/types"

export interface CartItem {
  product: Product
  quantity: number
}
interface CartState {
  items: CartItem[]

  addToCart: (product: Product) => void
  removeFromCart: (productId: number) => void
  updateQuantity: (productId: number, quantity: number) => void
  clearCart: () => void

  getTotalPrice: () => number
  getTotalItems: () => number
}
export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      addToCart: (product: Product) => {
        const { items } = get()
        const existingItem = items.find(
          (item) => item.product.id === product.id,
        )
        if (existingItem) {
          if (existingItem.quantity < product.stock) {
            set({
              items: items.map((item) =>
                item.product.id === product.id
                  ? { ...item, quantity: item.quantity + 1 }
                  : item,
              ),
            })
          }
        } else {
          set({ items: [...items, { product, quantity: 1 }] })
        }
      },
      removeFromCart: (productId: number) => {
        set({
          items: get().items.filter((item) => item.product.id !== productId),
        })
      },
      updateQuantity: (productId: number, quantity: number) => {
        if (quantity <= 0) return

        set({
          items: get().items.map((item) =>
            item.product.id === productId
              ? { ...item, quantity: Math.min(quantity, item.product.stock) }
              : item,
          ),
        })
      },
      clearCart: () => {
        set({ items: [] })
      },
      getTotalPrice: () => {
        return get().items.reduce(
          (total, item) => total + item.product.price * item.quantity,
          0,
        )
      },
      getTotalItems: () => {
        return get().items.reduce((total, item) => total + item.quantity, 0)
      },
    }),
    {
      name: "cart-storage",
    },
  ),
)
