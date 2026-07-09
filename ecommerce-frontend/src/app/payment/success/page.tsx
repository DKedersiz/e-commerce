"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { CheckCircle2, ShoppingBag, Package } from "lucide-react"

export default function PaymentSuccessPage() {
  const router = useRouter()

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 font-sans">
      <Card className="max-w-lg w-full text-center border-0 shadow-2xl animate-in zoom-in duration-500 overflow-hidden bg-white rounded-3xl">
        {/* Yeşil Header */}
        <div className="bg-green-500 py-12 flex justify-center">
          <div className="bg-white p-4 rounded-full shadow-lg">
            <CheckCircle2 className="w-20 h-20 text-green-500" />
          </div>
        </div>

        <CardContent className="pt-10 pb-12 px-8">
          <h1 className="text-4xl font-black text-slate-800 mb-4 tracking-tight">
            Ödeme Başarılı!
          </h1>
          <p className="text-base text-slate-500 mb-10 leading-relaxed">
            Siparişiniz başarıyla alındı ve ödemeniz onaylandı. Siparişinizin
            durumunu dilediğiniz zaman "Siparişlerim" sayfasından takip
            edebilirsiniz.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button
              size="lg"
              className="rounded-full shadow-md text-base px-8 h-14 bg-slate-900 text-white hover:bg-slate-800"
              onClick={() => router.push("/")}
            >
              <ShoppingBag className="w-5 h-5 mr-2" /> Alışverişe Dön
            </Button>
            <Button
              size="lg"
              variant="outline"
              className="rounded-full shadow-sm text-base px-8 h-14 border-slate-200 text-slate-700 hover:bg-slate-50"
              onClick={() => router.push("/orders")}
            >
              <Package className="w-5 h-5 mr-2" /> Siparişlerim
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
