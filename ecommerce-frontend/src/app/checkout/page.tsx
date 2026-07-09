"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useCartStore } from "@/store/cartStore"
import { useAuthStore } from "@/store/authStore"
import api from "@/lib/api"
import { Order } from "@/types"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  ArrowLeft,
  CreditCard,
  MapPin,
  Loader2,
  ShoppingBag,
} from "lucide-react"

export default function CheckoutPage() {
  const router = useRouter()
  const { isAuthenticated, initializeAuth } = useAuthStore()
  const { items, getTotalPrice, clearCart } = useCartStore()

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  const [address, setAddress] = useState(
    "Antigravity Teknopark, No: 42, İstanbul",
  )

  useEffect(() => {
    initializeAuth()
    if (items.length === 0) {
      router.push("/")
    }
  }, [initializeAuth, items.length, router])

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    try {
      // 1. Siparişi veritabanında oluştur
      const orderPayload = {
        items: items.map((item) => ({
          productId: item.product.id,
          quantity: item.quantity,
        })),
      }

      const createResponse = await api.post<Order>(
        "/orders/create",
        orderPayload,
      )
      const createdOrderId = createResponse.data.id

      // 2. Sipariş ID'si ile Stripe Checkout yönlendirme linkini al
      const sessionResponse = await api.post<{ url: string }>(
        `/payments/create-checkout-session/${createdOrderId}`,
      )

      // Sepeti temizle (Ödeme Stripe tarafında tamamlanacak)
      clearCart()

      // 3. Kullanıcıyı Stripe Güvenli Ödeme sayfasına uçur!
      window.location.href = sessionResponse.data.url
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
          "Ödeme oturumu başlatılırken bir hata oluştu.",
      )
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 py-8 px-4 font-sans">
      <div className="container mx-auto max-w-5xl">
        {/* Header */}
        <div className="flex items-center gap-4 mb-8">
          <Button
            variant="outline"
            size="icon"
            className="rounded-full shadow-sm hover:shadow-md"
            onClick={() => router.push("/cart")}
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-3xl font-extrabold text-slate-800 tracking-tight">
            Güvenli Ödeme
          </h1>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-4 rounded-xl border border-red-200 text-center font-medium mb-6">
            {error}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <form onSubmit={handlePayment} className="space-y-6">
            {/* Adres Bilgisi */}
            <Card className="border-0 shadow-sm rounded-2xl overflow-hidden bg-white">
              <CardHeader className="pb-3 border-b border-slate-100">
                <CardTitle className="flex items-center gap-2 text-lg font-bold text-slate-800">
                  <MapPin className="text-slate-900" size={20} /> Teslimat
                  Adresi
                </CardTitle>
              </CardHeader>
              <CardContent className="pt-5">
                <Input
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  required
                  className="bg-slate-50 border-slate-200 focus:border-slate-400 focus:ring-slate-900/10 rounded-xl py-3 h-12"
                />
              </CardContent>
            </Card>

            {/* Bilgilendirme Kartı */}
            <Card className="border-0 shadow-sm rounded-2xl bg-white p-6 space-y-4">
              <div className="flex items-start gap-4">
                <div className="bg-slate-100 p-3 rounded-full text-slate-900 shrink-0">
                  <CreditCard size={24} />
                </div>
                <div>
                  <h4 className="font-bold text-slate-800 text-sm">
                    Stripe ile Güvenli Ödeme
                  </h4>
                  <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                    Ödemeniz, dünya standartlarında siber güvenlik önlemleri
                    sunan **Stripe** altyapısıyla şifrelenerek gerçekleştirilir.
                    Kredi kartı bilgileriniz sunucularımızda asla tutulmaz.
                  </p>
                </div>
              </div>
            </Card>

            {/* Ödeme Butonu */}
            <Button
              type="submit"
              className="w-full h-14 text-base font-bold bg-slate-900 text-white hover:bg-slate-800 rounded-xl shadow-lg hover:shadow-xl transition-all flex items-center justify-center gap-2 active:scale-[0.99] disabled:opacity-50"
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />{" "}
                  Yönlendiriliyorsunuz...
                </>
              ) : (
                <>
                  <ShoppingBag size={20} />{" "}
                  {getTotalPrice().toLocaleString("tr-TR")} ₺ Öde
                </>
              )}
            </Button>
          </form>

          {/* Sipariş Özeti */}
          <div>
            <Card className="border-0 shadow-sm bg-white rounded-2xl overflow-hidden sticky top-24 border-t-4 border-t-slate-900">
              <CardHeader className="pb-3 border-b border-slate-100">
                <CardTitle className="text-lg font-bold text-slate-800">
                  Sipariş Özeti
                </CardTitle>
              </CardHeader>
              <CardContent className="pt-5">
                <div className="space-y-4 mb-6">
                  {items.map((item) => (
                    <div
                      key={item.product.id}
                      className="flex justify-between items-center text-sm"
                    >
                      <span className="text-slate-600 line-clamp-1 flex-1 pr-4">
                        {item.quantity}x {item.product.name}
                      </span>
                      <span className="font-bold font-mono text-slate-800">
                        {(item.product.price * item.quantity).toLocaleString(
                          "tr-TR",
                        )}{" "}
                        ₺
                      </span>
                    </div>
                  ))}
                </div>
                <div className="border-t border-slate-100 pt-4 flex justify-between items-center">
                  <span className="font-bold text-base text-slate-800">
                    Genel Toplam
                  </span>
                  <span className="text-2xl font-black text-slate-900 font-mono">
                    {getTotalPrice().toLocaleString("tr-TR")} ₺
                  </span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  )
}
