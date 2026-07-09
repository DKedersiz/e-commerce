# 🎨 Next.js 14 E-Ticaret Arayüzü (Storefront)

Bu proje; **Next.js 14 (App Router)**, **TypeScript**, **Zustand**, **Tailwind CSS** ve **Shadcn UI** kullanılarak geliştirilmiş, yüksek performanslı, mobil uyumlu ve modern bir e-ticaret arayüzüdür. Olay Güdümlü E-Ticaret platformumuzun kullanıcı tarafını temsil eder.

---

## 🚀 Temel Özellikler

* **Dinamik Katalog & Arama:** Anlık filtreleme ve kategori geçişlerine sahip hızlı ürün listeleme.
* **Ürün Detay Sayfası:** Dinamik yönlendirme (`/product/[id]`) kullanarak ürün açıklamalarını, ortalama puanları ve kullanıcı yorumlarını gösteren arayüz.
* **Etkileşimli Yorum Sistemi:** Giriş yapmış kullanıcılar için gerçek zamanlı 5 yıldızlı puan seçimi ve yorum gönderme formu.
* **Zustand ile Durum Yönetimi:** Tarayıcı oturumları arasında sepet verilerinin korunması ve kullanıcı giriş durumu yönetimi.
* **Stripe Hosted Checkout Entegrasyonu:** Güvenli ödeme için Stripe'ın hazır ödeme sayfasına yönlendirme ve ödeme sonucuna (Success/Cancel) göre kullanıcıyı karşılama.
* **Gemini AI Destekli Chatbot:** Kullanıcıların sorularına, veritabanındaki ürün kataloğunu analiz ederek gerçek zamanlı cevap ve ürün tavsiyesi veren yapay zeka widget'ı.

---

## 🛠️ Mimari ve Tasarım Kalıpları

### 1. Next.js 14 App Router ve Dizin Yapısı
Uygulama, performanslı sunucu/istemci taraflı renderlama (SSR/CSR) ve dinamik yönlendirme için App Router yapısını kullanır:
* `src/app/product/[id]/page.tsx`: Dinamik ürün detay sayfası.
* `src/app/cart/page.tsx` & `src/app/checkout/page.tsx`: Sepet ve ödemeye geçiş sayfaları.
* `src/app/payment/success` & `src/app/payment/failure`: Ödeme sonrası başarılı/başarısız yönlendirme sayfaları.

### 2. Zustand ile Durum Yönetimi (State Management)
Uygulamayı karmaşık Redux sağlayıcıları (providers) ile sarmalamak yerine, hafif ve yüksek performanslı **Zustand** kütüphanesini tercih ettik:
* **Kimlik Doğrulama Deposu (`src/store/authStore.ts`):** JWT token'ını `localStorage` ile senkronize eder ve giriş/çıkış durumlarını tüm uygulamaya sunar.
* **Sepet Deposu (`src/store/cartStore.ts`):** Sepetteki ürünleri yönetir, toplam tutarları hesaplar ve sayfa yenilense dahi verileri tarayıcı hafızasında saklar.

### 3. API Interceptor Katmanı (Axios)
Güvenlik ve oturum kontrolü sağlamak amacıyla istek ve yanıt interceptor'ları [api.ts](file:///D:/Projects/ecommerce-frontend/src/lib/api.ts) dosyasında yapılandırılmıştır:
* **Request Interceptor (İstek Yakalayıcı):** Her istek gönderilmeden önce `localStorage` üzerindeki JWT token'ını okur ve isteğin başlığına otomatik olarak `Authorization: Bearer <token>` bilgisini ekler.
* **Response Interceptor (Yanıt Yakalayıcı):** Backend'den gelen `401 Unauthorized` veya `403 Forbidden` yanıtlarını izler. Eğer token süresi dolmuşsa, oturum verilerini temizler ve kullanıcıyı anında `/login` sayfasına yönlendirir.

---

## ⚙️ Yerel Kurulum Adımları

Bilgisayarınızda Node.js (sürüm 20+) kurulu olduğundan emin olun.

### 1. Bağımlılıkları Yükleme
```bash
npm install
```

### 2. Geliştirme Sunucusunu Başlatma
Spring Boot backend API'sinin `http://localhost:8080` adresinde çalıştığından emin olun.
```bash
npm run dev
```
Arayüz http://localhost:3000 adresinde aktif hale gelecektir.

---

## 🐳 Docker Üretim (Production) Yapılandırması
Uygulama, üretim ortamında çalıştırılmak üzere çok aşamalı (multi-stage) bir Dockerfile ile optimize edilmiştir:
* **Aşama 1 (deps):** Üretim ve derleme bağımlılıklarını kurar.
* **Aşama 2 (builder):** Next.js uygulamasını `output: 'standalone'` olarak derler.
* **Aşama 3 (runner):** Next.js sunucu ve statik dosyalarını, root yetkisi olmayan güvenli `nextjs` kullanıcısı ile minimal bir alpine imajında `3000` portundan servis eder.

Docker ile çalıştırmak için:
```bash
docker build -t ecommerce-frontend .
docker run -p 3000:3000 --env NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1 ecommerce-frontend
```
Alternatif olarak, ana dizindeki `docker-compose.yml` dosyasını kullanarak tüm sistemle birlikte tek seferde başlatabilirsiniz.
