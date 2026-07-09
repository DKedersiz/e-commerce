"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuthStore } from "@/store/authStore"
import { useCartStore } from "@/store/cartStore"
import api from "@/lib/api"
import { Product, PageResponse, Category } from "@/types"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { ShoppingCart, LogOut, Package, Star } from "lucide-react"

export default function ProductsPage() {
  const router = useRouter()
  const { isAuthenticated, logout, initializeAuth } = useAuthStore()

  const { addToCart, getTotalItems } = useCartStore()
  const totalItems = getTotalItems()

  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [categories, setCategories] = useState<Category[]>([])
  const [selectedCategorySlug, setSelectedCategorySlug] = useState<
    string | null
  >(null)

  useEffect(() => {
    initializeAuth()
  }, [initializeAuth])

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true)
        const response = await api.get<PageResponse<Product>>("/products", {
          params: {
            page: page,
            size: 8,
            category: selectedCategorySlug,
          },
        })
        setProducts(response.data.content)
        setTotalPages(response.data.totalPages)
      } catch (err) {
        console.error("Ürünler yüklenemedi", err)
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [isAuthenticated, page, selectedCategorySlug])

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get<Category[]>("/categories")
        setCategories(response.data)
      } catch (err) {
        console.error("Kategoriler yüklenemedi", err)
      }
    }
    fetchCategories()
  }, [])

  if (loading)
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="animate-pulse flex flex-col items-center gap-4">
          <div className="h-12 w-12 rounded-full border-4 border-primary border-t-transparent animate-spin"></div>
          <p className="text-slate-500 font-medium tracking-wider">
            Vitrin Hazırlanıyor...
          </p>
        </div>
      </div>
    )

  return (
    <div className="min-h-screen bg-slate-50 font-sans pb-16">
      <header className="sticky top-0 z-50 w-full border-b bg-white/80 backdrop-blur-md shadow-sm">
        <div className="container mx-auto max-w-7xl px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-6">
            <h1
              className="text-2xl font-black text-slate-900 tracking-tight cursor-pointer hover:text-slate-700 transition-colors"
              onClick={() => router.push("/")}
            >
              DogukanShop
            </h1>

            <div
              className="relative cursor-pointer hover:scale-110 transition-transform"
              onClick={() => router.push("/cart")}
            >
              <ShoppingCart className="w-8 h-8 text-primary" />
              {totalItems > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-[10px] font-bold w-5 h-5 flex items-center justify-center rounded-full border-2 border-white shadow-sm">
                  {totalItems}
                </span>
              )}
            </div>
          </div>

          <div className="flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <Button
                  variant="outline"
                  className="border-primary/20 text-primary hover:bg-primary/5 rounded-full"
                  onClick={() => router.push("/orders")}
                >
                  <Package className="w-4 h-4 mr-2" /> Siparişlerim
                </Button>
                <Button
                  variant="ghost"
                  className="text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-full"
                  onClick={() => {
                    logout()
                    router.push("/login")
                  }}
                >
                  <LogOut className="w-4 h-4 mr-2" /> Çıkış
                </Button>
              </>
            ) : (
              <Button
                className="rounded-full px-6 shadow-md hover:shadow-lg transition-all"
                onClick={() => router.push("/login")}
              >
                Giriş Yap
              </Button>
            )}
          </div>
        </div>
      </header>

      <main className="container mx-auto max-w-7xl px-4 mt-8">
        {categories.length > 0 && (
          <div className="mb-8">
            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-4">
              Kategoriler
            </h3>
            <div className="flex overflow-x-auto gap-3 pb-2 hide-scrollbar">
              <Button
                variant={selectedCategorySlug === null ? "default" : "outline"}
                className={`rounded-full shrink-0 ${selectedCategorySlug === null ? "shadow-md" : "text-slate-600 bg-white"}`}
                onClick={() => {
                  setSelectedCategorySlug(null)
                  setPage(0)
                }}
              >
                Tüm Ürünler
              </Button>

              {categories.map((cat) => (
                <Button
                  key={cat.id}
                  variant={
                    selectedCategorySlug === cat.slug ? "default" : "outline"
                  }
                  className={`rounded-full shrink-0 transition-all ${selectedCategorySlug === cat.slug ? "shadow-md" : "text-slate-600 bg-white hover:bg-slate-100"}`}
                  onClick={() => {
                    setSelectedCategorySlug(cat.slug)
                    setPage(0)
                  }}
                >
                  {cat.name}
                </Button>
              ))}
            </div>
          </div>
        )}

        <h2 className="text-3xl font-extrabold text-slate-800 mb-8">
          {selectedCategorySlug
            ? `${categories.find((c) => c.slug === selectedCategorySlug)?.name} Kategorisi`
            : "Sizin İçin Seçilenler"}
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map((product) => (
            <Card
              key={product.id}
              onClick={() => router.push(`/product/${product.id}`)}
              className="group overflow-hidden rounded-2xl border-0 shadow-sm hover:shadow-xl transition-all duration-300 bg-white cursor-pointer"
            >
              <div className="relative h-48 w-full bg-slate-100 overflow-hidden">
                <img
                  src={`https://picsum.photos/seed/${product.id + 100}/400/300`}
                  alt={product.name}
                  className="object-cover w-full h-full group-hover:scale-110 transition-transform duration-500"
                />
                {product.stock < 10 && product.stock > 0 && (
                  <Badge className="absolute top-3 left-3 bg-red-500/90 hover:bg-red-500 shadow-sm">
                    Son {product.stock} Ürün!
                  </Badge>
                )}
              </div>

              <CardContent className="p-5 flex flex-col justify-between h-[210px]">
                <div>
                  <h3 className="font-bold text-lg text-slate-800 line-clamp-1 mb-1 group-hover:text-slate-900 transition-colors">
                    {product.name}
                  </h3>

                  <div className="flex items-center gap-1.5 mb-2">
                    <div className="flex text-yellow-500">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <Star
                          key={i}
                          size={12}
                          fill={
                            i < Math.round(product.averageRating || 0)
                              ? "currentColor"
                              : "none"
                          }
                        />
                      ))}
                    </div>
                    {product.averageRating && product.averageRating > 0 ? (
                      <span className="text-[11px] font-bold text-slate-500">
                        {product.averageRating.toFixed(1)} (
                        {product.reviewCount})
                      </span>
                    ) : (
                      <span className="text-[11px] text-slate-400">
                        Henüz yorum yok
                      </span>
                    )}
                  </div>

                  <p className="text-sm text-slate-500 line-clamp-2">
                    {product.description}
                  </p>
                </div>

                <div className="flex items-center justify-between mt-4">
                  <span className="text-2xl font-black text-slate-900">
                    {product.price.toLocaleString("tr-TR")}{" "}
                    <span className="text-lg">₺</span>
                  </span>
                  <Button
                    className="rounded-full px-6 shadow-md hover:shadow-lg transition-all active:scale-95 bg-slate-900 text-white hover:bg-slate-800"
                    onClick={(e) => {
                      e.stopPropagation()
                      addToCart(product)
                    }}
                    disabled={product.stock === 0}
                  >
                    {product.stock === 0 ? "Tükendi" : "Sepete Ekle"}
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center items-center gap-4 mt-12">
            <Button
              variant="outline"
              className="rounded-full px-6"
              onClick={() => setPage((p) => p - 1)}
              disabled={page === 0}
            >
              Önceki
            </Button>
            <div className="flex gap-2">
              {Array.from({ length: totalPages }).map((_, i) => (
                <div
                  key={i}
                  className={`w-2.5 h-2.5 rounded-full transition-colors ${i === page ? "bg-primary" : "bg-slate-200"}`}
                />
              ))}
            </div>
            <Button
              variant="outline"
              className="rounded-full px-6"
              onClick={() => setPage((p) => p + 1)}
              disabled={page >= totalPages - 1}
            >
              Sonraki
            </Button>
          </div>
        )}
      </main>
    </div>
  )
}
