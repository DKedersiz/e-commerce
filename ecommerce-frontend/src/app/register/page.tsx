"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import api from "@/lib/api"
import { useAuthStore } from "@/store/authStore"
import { ArrowLeft, CheckCircle2 } from "lucide-react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Label } from "@/components/ui/label"
import { useState } from "react"

export default function RegisterPage() {
  const router = useRouter()
  const { login } = useAuthStore()

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState(false)

  const [firstName, setFirstName] = useState("")
  const [lastName, setLastName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    try {
      const response = await api.post("/auth/register", {
        firstName,
        lastName,
        email,
        password,
      })

      const token = response.data.token

      login(token)
      setSuccess(true)

      setTimeout(() => {
        router.push("/")
      }, 2000)
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
          "Kayıt olurken bir hata oluştu. Şifre kurallarına uyduğunuzdan emin olun.",
      )
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 relative">
        <div className="bg-white p-10 rounded-3xl shadow-xl text-center max-w-md w-full animate-in zoom-in duration-500">
          <CheckCircle2 className="w-24 h-24 text-green-500 mx-auto mb-6" />
          <h1 className="text-3xl font-black text-slate-800 mb-2">
            Aramıza Hoş Geldin!
          </h1>
          <p className="text-slate-500">
            Kayıt işlemin başarıyla tamamlandı. Ana sayfaya
            yönlendiriliyorsun...
          </p>
        </div>
      </div>
    )
  }
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 relative p-4">
      <div className="absolute top-6 left-6 md:top-10 md:left-10">
        <Button
          variant="ghost"
          onClick={() => router.push("/")}
          className="text-slate-500 hover:text-primary"
        >
          <ArrowLeft className="w-5 h-5 mr-2" />
          Ana Sayfaya Dön
        </Button>
      </div>
      <Card className="w-full max-w-md shadow-lg border-0 rounded-2xl overflow-hidden">
        <CardHeader className="bg-primary/5 pb-8">
          <CardTitle className="text-3xl font-black text-center text-slate-800 mt-4">
            Kayıt Ol
          </CardTitle>
          <p className="text-center text-sm text-slate-500 mt-2">
            Saniyeler içinde hesabını oluştur ve alışverişe başla.
          </p>
        </CardHeader>
        <CardContent className="pt-6">
          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">Ad</Label>
                <Input
                  id="firstName"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Soyad</Label>
                <Input
                  id="lastName"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="ornek@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Şifre</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <p className="text-xs text-slate-400 font-medium leading-relaxed">
                * En az 8 karakter, 1 büyük harf, 1 küçük harf ve 1 rakam
                içermelidir.
              </p>
            </div>
            {error && (
              <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm font-bold text-center border border-red-200 animate-pulse">
                {error}
              </div>
            )}
            <Button
              type="submit"
              className="w-full h-12 text-lg rounded-xl shadow-md transition-all hover:shadow-lg active:scale-95"
              disabled={loading}
            >
              {loading ? "Hesap Oluşturuluyor..." : "Kayıt Ol"}
            </Button>
          </form>
          <div className="mt-6 text-center text-sm text-slate-500">
            Zaten hesabın var mı?{" "}
            <Link
              href="/login"
              className="text-primary font-bold hover:underline transition-all"
            >
              Giriş Yap
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
