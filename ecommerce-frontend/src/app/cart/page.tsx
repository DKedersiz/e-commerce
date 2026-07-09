"use client"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useCartStore } from "@/store/cartStore"
import { useAuthStore } from "@/store/authStore"
import api from "@/lib/api"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Trash2, Plus, Minus, ArrowLeft, CreditCard } from "lucide-react"
export default function CartPage() {
  const router = useRouter()
  const { isAuthenticated, initializeAuth } = useAuthStore()
  const { items, removeFromCart, updateQuantity, getTotalPrice, clearCart } =
    useCartStore()

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  useEffect(() => {
    initializeAuth()
  }, [initializeAuth])

  const handleCheckout = () => {
    if (items.length === 0) return

    if (!isAuthenticated) {
      router.push("/login")
      return
    }
    router.push("/checkout")
  }

  return (
    <div className="min-h-screen bg-slate-50 py-8 px-4 font-sans">
      <div className="container mx-auto max-w-5xl">
        <div className="flex items-center gap-4 mb-8">
          <Button
            variant="outline"
            size="icon"
            className="rounded-full shadow-sm"
            onClick={() => router.push("/")}
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-3xl font-extrabold text-slate-800 tracking-tight">
            Sepetim
          </h1>
        </div>
        {error && (
          <div className="bg-red-50 text-red-600 p-4 rounded-xl border border-red-200 text-center font-medium mb-6">
            {error}
          </div>
        )}
        {items.length === 0 ? (
          <Card className="border-dashed border-2 border-slate-200 bg-transparent shadow-none text-center py-20">
            <h3 className="text-2xl font-bold text-slate-500 mb-4">
              Sepetiniz şu an boş
            </h3>
            <p className="text-slate-400 mb-8">
              Görünüşe göre henüz bir ürün eklemediniz.
            </p>
            <Button
              size="lg"
              className="rounded-full px-8"
              onClick={() => router.push("/")}
            >
              Alışverişe Başla
            </Button>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              {items.map((item) => (
                <Card
                  key={item.product.id}
                  className="border-0 shadow-sm hover:shadow-md transition-shadow"
                >
                  <CardContent className="p-4 flex flex-col sm:flex-row items-center gap-4">
                    <div className="w-24 h-24 bg-slate-100 rounded-lg overflow-hidden shrink-0">
                      <img
                        src={`https://picsum.photos/seed/${item.product.id + 100}/200`}
                        alt={item.product.name}
                        className="w-full h-full object-cover"
                      />
                    </div>
                    <div className="flex-1 text-center sm:text-left">
                      <h3 className="font-bold text-lg text-slate-800">
                        {item.product.name}
                      </h3>
                      <p className="text-primary font-black text-xl mt-1">
                        {item.product.price.toLocaleString("tr-TR")} ₺
                      </p>
                    </div>
                    <div className="flex items-center gap-3 bg-slate-50 rounded-full border p-1">
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 rounded-full"
                        onClick={() =>
                          updateQuantity(item.product.id, item.quantity - 1)
                        }
                      >
                        <Minus className="w-4 h-4" />
                      </Button>
                      <span className="font-bold w-4 text-center">
                        {item.quantity}
                      </span>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 rounded-full"
                        onClick={() =>
                          updateQuantity(item.product.id, item.quantity + 1)
                        }
                      >
                        <Plus className="w-4 h-4" />
                      </Button>
                    </div>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="text-red-500 hover:text-red-700 hover:bg-red-50 rounded-full"
                      onClick={() => removeFromCart(item.product.id)}
                    >
                      <Trash2 className="w-5 h-5" />
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
            <div className="lg:col-span-1">
              <Card className="border-0 shadow-md sticky top-24 border-t-4 border-t-primary">
                <CardContent className="p-6">
                  <h3 className="text-xl font-bold text-slate-800 border-b pb-4 mb-4">
                    Sipariş Özeti
                  </h3>

                  <div className="flex justify-between items-center mb-2">
                    <span className="text-slate-500">Ara Toplam</span>
                    <span className="font-bold">
                      {getTotalPrice().toLocaleString("tr-TR")} ₺
                    </span>
                  </div>
                  <div className="flex justify-between items-center mb-6">
                    <span className="text-slate-500">Kargo</span>
                    <span className="text-green-500 font-bold">Ücretsiz</span>
                  </div>

                  <div className="flex justify-between items-center border-t pt-4 mb-8">
                    <span className="text-lg font-bold text-slate-800">
                      Genel Toplam
                    </span>
                    <span className="text-3xl font-black text-primary">
                      {getTotalPrice().toLocaleString("tr-TR")} ₺
                    </span>
                  </div>
                  <Button
                    className="w-full h-14 text-lg rounded-full shadow-lg hover:shadow-xl transition-all"
                    disabled={loading}
                    onClick={handleCheckout}
                  >
                    {loading ? (
                      "Sipariş Onaylanıyor..."
                    ) : (
                      <>
                        <CreditCard className="mr-2 w-5 h-5" /> Siparişi Onayla
                      </>
                    )}
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
