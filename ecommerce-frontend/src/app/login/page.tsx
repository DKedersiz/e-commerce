"use client"

import api from "@/lib/api"
import { useAuthStore } from "@/store/authStore"
import { AuthResponse } from "@/types"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ArrowLeft } from "lucide-react"

export default function LoginPage() {
  const router = useRouter()
  const login = useAuthStore((state) => state.login)

  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setLoading(true)

    try {
      const response = await api.post<AuthResponse>("/auth/login", {
        email: email,
        password: password,
      })

      login(response.data.accessToken)

      router.push("/")
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message ||
        "Giriş başarısız bilgilerinizi kontrol ediniz."
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 relative">
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
      <Card className="w-full max-w-md shadow-lg">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Giriş Yap</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
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
            </div>
            {error && (
              <p className="text-sm text-red-500 text-center font-medium">
                {error}
              </p>
            )}
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Giriş yapılıyor..." : "Giriş Yap"}
            </Button>
          </form>
          <div className="mt-6 text-center text-sm">
            <span
              onClick={() => router.push("/register")}
              className="text-slate-500 hover:text-primary transition-colors block p-2 flex items-center justify-center gap-1 cursor-pointer"
            >
              <span>Hesabın yok mu?</span>
              <span className="font-bold underline">Kayıt Ol</span>
            </span>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
