"use client"

import { useEffect, useState, use } from "react"
import { useRouter } from "next/navigation"
import { useAuthStore } from "@/store/authStore"
import { useCartStore } from "@/store/cartStore"
import api from "@/lib/api"
import { Product, ReviewResponse } from "@/types"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent } from "@/components/ui/card"
import {
  ShoppingCart,
  LogOut,
  Package,
  Star,
  MessageSquare,
  Loader2,
  ArrowLeft,
} from "lucide-react"

export default function ProductDetailPage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const router = useRouter()
  const { id } = use(params)
  const { isAuthenticated, logout, initializeAuth } = useAuthStore()
  const { addToCart, getTotalItems } = useCartStore()
  const totalItems = getTotalItems()

  const [product, setProduct] = useState<Product | null>(null)
  const [reviews, setReviews] = useState<ReviewResponse[]>([])
  const [loading, setLoading] = useState(true)

  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState("")
  const [submitting, setSubmitting] = useState(false)
  const [reviewError, setReviewError] = useState<string | null>(null)

  useEffect(() => {
    initializeAuth()
  }, [initializeAuth])

  const fetchData = async () => {
    try {
      setLoading(true)
      const [prodRes, reviewsRes] = await Promise.all([
        api.get<Product>(`/products/${id}`),
        api.get<ReviewResponse[]>(`/reviews/product/${id}`),
      ])
      setProduct(prodRes.data)
      setReviews(reviewsRes.data)
    } catch (err) {
      console.error("Veriler yüklenemedi", err)
      router.push("/")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [id])

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setReviewError(null)
    setSubmitting(true)

    try {
      await api.post("/reviews", {
        productId: Number(id),
        rating,
        comment,
      })
      setComment("")
      setRating(5)

      const reviewsRes = await api.get<ReviewResponse[]>(
        `/reviews/product/${id}`,
      )
      setReviews(reviewsRes.data)

      const prodRes = await api.get<Product>(`/products/${id}`)
      setProduct(prodRes.data)
    } catch (err: any) {
      const errMsg =
        err.response?.data?.message || "Yorum eklenirken bir hata oluştu."
      setReviewError(errMsg)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="animate-pulse flex flex-col items-center gap-4">
          <div className="h-12 w-12 rounded-full border-4 border-slate-900 border-t-transparent animate-spin"></div>
          <p className="text-slate-500 font-medium tracking-wider">
            Detaylar Yükleniyor...
          </p>
        </div>
      </div>
    )
  }

  if (!product) return null

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
              <ShoppingCart className="w-8 h-8 text-slate-900" />
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
                  className="border-slate-200 text-slate-700 hover:bg-slate-50 rounded-full"
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
                className="rounded-full px-6 shadow-md bg-slate-900 text-white hover:bg-slate-800"
                onClick={() => router.push("/login")}
              >
                Giriş Yap
              </Button>
            )}
          </div>
        </div>
      </header>

      <main className="container mx-auto max-w-5xl px-4 mt-8">
        <button
          onClick={() => router.push("/")}
          className="flex items-center gap-2 text-slate-600 hover:text-slate-950 font-semibold mb-6 transition-colors"
        >
          <ArrowLeft size={18} /> Geri Dön
        </button>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-10 bg-white p-8 rounded-3xl border border-slate-200/60 shadow-sm">
          {/* Image */}
          <div className="relative h-96 w-full rounded-2xl overflow-hidden bg-slate-100 border border-slate-100 shadow-inner">
            <img
              src={`https://picsum.photos/seed/${product.id + 100}/800/600`}
              alt={product.name}
              className="object-cover w-full h-full"
            />
          </div>

          <div className="flex flex-col justify-between">
            <div className="space-y-4">
              <Badge className="bg-slate-100 text-slate-800 border-none px-3 py-1 hover:bg-slate-100">
                {product.categoryName || "Genel"}
              </Badge>
              <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                {product.name}
              </h2>

              <div className="flex items-center gap-2">
                <div className="flex text-yellow-500">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star
                      key={i}
                      size={18}
                      fill={
                        i < Math.round(product.averageRating || 0)
                          ? "currentColor"
                          : "none"
                      }
                      className="color-current"
                    />
                  ))}
                </div>
                {product.averageRating && product.averageRating > 0 ? (
                  <span className="text-sm font-bold text-slate-700">
                    {product.averageRating.toFixed(1)}{" "}
                    <span className="text-slate-400 font-normal">
                      ({product.reviewCount} Değerlendirme)
                    </span>
                  </span>
                ) : (
                  <span className="text-sm text-slate-400">
                    Henüz değerlendirilmemiş
                  </span>
                )}
              </div>

              <hr className="border-slate-100" />

              <div>
                <h4 className="text-xs font-extrabold text-slate-400 uppercase tracking-wider mb-2">
                  Ürün Açıklaması
                </h4>
                <p className="text-slate-600 leading-relaxed text-sm">
                  {product.description}
                </p>
              </div>

              <div>
                <h4 className="text-xs font-extrabold text-slate-400 uppercase tracking-wider mb-2">
                  Satıcı
                </h4>
                <p className="text-slate-800 text-sm font-semibold">
                  DogukanShop A.Ş.
                </p>
              </div>
            </div>

            <div className="pt-6 border-t border-slate-100 mt-6 flex items-center justify-between">
              <div>
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">
                  Fiyat
                </span>
                <span className="text-3xl font-black text-slate-900">
                  {product.price.toLocaleString("tr-TR")}{" "}
                  <span className="text-xl">₺</span>
                </span>
              </div>
              <Button
                size="lg"
                className="rounded-full px-8 bg-slate-900 text-white hover:bg-slate-800 shadow-md transition-all active:scale-95 disabled:opacity-50"
                onClick={() => addToCart(product)}
                disabled={product.stock === 0}
              >
                {product.stock === 0 ? "Tükendi" : "Sepete Ekle"}
              </Button>
            </div>
          </div>
        </div>

        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8 items-start">
          <div className="md:col-span-2 space-y-6">
            <h3 className="text-xl font-bold text-slate-900 flex items-center gap-2">
              <MessageSquare size={20} /> Müşteri Yorumları ({reviews.length})
            </h3>

            {reviews.length === 0 ? (
              <div className="bg-white p-8 rounded-2xl border border-slate-200/60 shadow-sm text-center">
                <p className="text-slate-400 text-sm">
                  Bu ürüne henüz yorum yapılmamış. İlk yorumu sen yap!
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                {reviews.map((review) => (
                  <Card
                    key={review.id}
                    className="border border-slate-200/60 rounded-2xl shadow-sm bg-white overflow-hidden"
                  >
                    <CardContent className="p-5 space-y-3">
                      <div className="flex justify-between items-start">
                        <div>
                          <h5 className="font-bold text-slate-800 text-sm">
                            {review.userName}
                          </h5>
                          <span className="text-[10px] text-slate-400">
                            {new Date(review.createdAt).toLocaleDateString(
                              "tr-TR",
                            )}
                          </span>
                        </div>
                        <div className="flex text-yellow-500">
                          {Array.from({ length: 5 }).map((_, i) => (
                            <Star
                              key={i}
                              size={14}
                              fill={i < review.rating ? "currentColor" : "none"}
                            />
                          ))}
                        </div>
                      </div>
                      <p className="text-slate-600 text-sm leading-relaxed">
                        {review.comment}
                      </p>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/60 shadow-sm space-y-4">
            <h4 className="font-bold text-slate-900 text-lg">
              Bu Ürünü Değerlendir
            </h4>

            {isAuthenticated ? (
              <form onSubmit={handleReviewSubmit} className="space-y-4">
                {reviewError && (
                  <div className="p-3 bg-red-50 text-red-600 text-xs rounded-xl border border-red-100 font-medium">
                    {reviewError}
                  </div>
                )}

                <div>
                  <label className="text-xs font-bold text-slate-400 uppercase tracking-wider block mb-2">
                    Puanın
                  </label>
                  <div className="flex gap-2">
                    {Array.from({ length: 5 }).map((_, i) => {
                      const starValue = i + 1
                      return (
                        <button
                          key={i}
                          type="button"
                          onClick={() => setRating(starValue)}
                          className="text-yellow-500 hover:scale-110 transition-transform"
                        >
                          <Star
                            size={28}
                            fill={starValue <= rating ? "currentColor" : "none"}
                          />
                        </button>
                      )
                    })}
                  </div>
                </div>

                <div>
                  <label className="text-xs font-bold text-slate-400 uppercase tracking-wider block mb-2">
                    Yorumun
                  </label>
                  <textarea
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="Ürün hakkındaki görüşlerinizi yazın..."
                    className="w-full min-h-[100px] p-3 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-slate-900/10 focus:border-slate-900 transition-all bg-slate-50"
                    maxLength={1000}
                    required
                  />
                </div>

                <Button
                  type="submit"
                  disabled={submitting}
                  className="w-full rounded-full bg-slate-900 text-white hover:bg-slate-800 shadow-md"
                >
                  {submitting ? (
                    <>
                      <Loader2 size={16} className="animate-spin mr-2" />{" "}
                      Gönderiliyor...
                    </>
                  ) : (
                    "Yorumu Gönder"
                  )}
                </Button>
              </form>
            ) : (
              <div className="text-center py-4 space-y-3">
                <p className="text-slate-400 text-xs">
                  Yorum yapabilmek için önce giriş yapmalısınız.
                </p>
                <Button
                  variant="outline"
                  className="rounded-full w-full border-slate-200 text-slate-700 hover:bg-slate-50"
                  onClick={() => router.push("/login")}
                >
                  Giriş Yap
                </Button>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}
