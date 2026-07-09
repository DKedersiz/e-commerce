"use client"
import { useState, useRef, useEffect } from "react"
import { Bot, X, Send, ShoppingBag, Loader2 } from "lucide-react"
import api from "@/lib/api"
import { Product, AiChatResponse } from "@/types"
import { useCartStore } from "@/store/cartStore"
interface ChatMessage {
  id: string
  sender: "user" | "ai"
  text: string
  products?: Product[]
}
export default function AiChatWidget() {
  const [isOpen, setIsOpen] = useState(false)
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: "welcome",
      sender: "ai",
      text: "Merhaba! Ben DogukanShop AI asistanınız. Size nasıl yardımcı olabilirim?",
    },
  ])
  const [input, setInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const { addToCart } = useCartStore()
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }
  useEffect(() => {
    scrollToBottom()
  }, [messages])
  const handleSend = async () => {
    if (!input.trim()) return
    const userMessage = input.trim()
    setInput("")

    setMessages((prev) => [
      ...prev,
      { id: Date.now().toString(), sender: "user", text: userMessage },
    ])
    setIsLoading(true)
    try {
      const { data: aiResponse } = await api.post<AiChatResponse>("/ai/chat", {
        message: userMessage,
      })

      let recommendedProducts: Product[] = []
      if (
        aiResponse.recommendedProductIds &&
        aiResponse.recommendedProductIds.length > 0
      ) {
        const productPromises = aiResponse.recommendedProductIds.map((id) =>
          api.get<Product>(`/products/${id}`),
        )
        const responses = await Promise.allSettled(productPromises)

        recommendedProducts = responses
          .filter(
            (res): res is PromiseFulfilledResult<any> =>
              res.status === "fulfilled",
          )
          .map((res) => res.value.data)
      }
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString() + "ai",
          sender: "ai",
          text: aiResponse.message,
          products: recommendedProducts,
        },
      ])
    } catch (error) {
      console.error("AI Hatası:", error)
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString() + "err",
          sender: "ai",
          text: "Üzgünüm, şu an bağlantı kuramıyorum. Lütfen daha sonra tekrar deneyin.",
        },
      ])
    } finally {
      setIsLoading(false)
    }
  }
  return (
    <div className="fixed bottom-6 right-6 z-50 font-sans">
      {/* Kapalı Mod (Premium Koyu Buton) */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="bg-slate-900 text-white p-4 rounded-full shadow-2xl hover:scale-110 transition-transform flex items-center justify-center animate-bounce"
        >
          <Bot size={28} />
        </button>
      )}
      {/* Açık Mod (Pencere) */}
      {isOpen && (
        <div
          className="w-[340px] sm:w-[380px] bg-white rounded-2xl shadow-2xl border border-slate-200 flex flex-col overflow-hidden transition-all duration-300"
          style={{ height: "550px" }}
        >
          {/* Header - Klişe gradient yerine premium koyu renk */}
          <div className="bg-slate-900 p-4 text-white flex justify-between items-center shadow-md z-10">
            <div className="flex items-center gap-3">
              <div className="bg-white/10 p-2 rounded-full">
                <Bot size={24} />
              </div>
              <h3 className="font-semibold tracking-wide">
                DogukanShop Asistan
              </h3>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="text-slate-400 hover:text-white hover:bg-white/10 p-1.5 rounded-full transition-colors"
            >
              <X size={20} />
            </button>
          </div>
          {/* Messages Area */}
          <div className="flex-1 overflow-y-auto p-4 space-y-5 bg-slate-50">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.sender === "user" ? "items-end" : "items-start"}`}
              >
                {/* Bubble */}
                <div
                  className={`max-w-[85%] p-3 rounded-2xl text-[14px] shadow-sm leading-relaxed ${
                    msg.sender === "user"
                      ? "bg-slate-800 text-white rounded-tr-sm"
                      : "bg-white text-slate-700 rounded-tl-sm border border-slate-200"
                  }`}
                >
                  {msg.text}
                </div>

                {/* Mini Product Cards */}
                {msg.products && msg.products.length > 0 && (
                  <div className="mt-3 w-[85%] space-y-2">
                    {msg.products.map((product) => (
                      <div
                        key={product.id}
                        className="bg-white p-2.5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-3 group hover:border-slate-400 transition-colors"
                      >
                        <img
                          src={`https://picsum.photos/seed/${product.id + 100}/100/100`}
                          className="w-14 h-14 rounded-lg object-cover border border-slate-100"
                          alt={product.name}
                        />
                        <div className="flex-1 min-w-0">
                          <h4 className="text-xs font-bold text-slate-800 truncate group-hover:text-slate-900 transition-colors">
                            {product.name}
                          </h4>
                          <p className="text-xs font-black text-slate-500 mt-1">
                            {product.price.toLocaleString("tr-TR")} ₺
                          </p>
                        </div>
                        <button
                          onClick={() => addToCart(product)}
                          className="p-2.5 bg-slate-100 hover:bg-slate-900 hover:text-white rounded-full transition-all text-slate-600 active:scale-95"
                          title="Sepete Ekle"
                        >
                          <ShoppingBag size={16} />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {/* Yükleniyor Animasyonu */}
            {isLoading && (
              <div className="flex items-start">
                <div className="bg-white p-3.5 rounded-2xl rounded-tl-sm border border-slate-200 shadow-sm flex items-center gap-3">
                  <Loader2 size={16} className="animate-spin text-slate-800" />
                  <span className="text-xs font-medium text-slate-500">
                    Yanıt aranıyor...
                  </span>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
          {/* Input Area */}
          <div className="p-3.5 bg-white border-t border-slate-100 flex items-center gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              placeholder="Bana bir şeyler sor..."
              className="flex-1 border border-slate-200 bg-slate-50 rounded-full px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-slate-400 transition-all"
              disabled={isLoading}
            />
            <button
              onClick={handleSend}
              disabled={!input.trim() || isLoading}
              className="bg-slate-900 text-white p-2.5 rounded-full shadow-md hover:shadow-lg hover:bg-slate-800 disabled:opacity-50 transition-all active:scale-95"
            >
              <Send size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
