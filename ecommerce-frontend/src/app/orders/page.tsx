"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import api from "@/lib/api";
import { Order } from "@/types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Package, Clock, CheckCircle, XCircle } from "lucide-react";

export default function OrdersPage() {
  const router = useRouter();
  const { isAuthenticated, initializeAuth } = useAuthStore();

  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    initializeAuth();
  }, [initializeAuth]);

  useEffect(() => {
    if (!isAuthenticated && localStorage.getItem("token") === null) {
      router.push("/login");
    }
  }, [isAuthenticated, router]);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await api.get<Order[]>("/orders/getOrders");

        const sortedOrders = response.data.sort(
          (a, b) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        );
        setOrders(sortedOrders);
      } catch (err) {
        setError("Siparişleriniz yüklenirken bir sorun oluştu.");
      } finally {
        setLoading(false);
      }
    };

    if (isAuthenticated) fetchOrders();
  }, [isAuthenticated]);

  const getStatusConfig = (status: string) => {
    switch (status) {
      case "COMPLETED":
        return {
          color: "bg-green-500 hover:bg-green-600",
          icon: <CheckCircle className="w-4 h-4 mr-1" />,
          label: "Tamamlandı",
        };
      case "PENDING":
        return {
          color: "bg-yellow-500 hover:bg-yellow-600",
          icon: <Clock className="w-4 h-4 mr-1" />,
          label: "Bekliyor",
        };
      case "FAILED":
        return {
          color: "bg-red-500 hover:bg-red-600",
          icon: <XCircle className="w-4 h-4 mr-1" />,
          label: "Başarısız",
        };
      default:
        return {
          color: "bg-slate-500",
          icon: <Package className="w-4 h-4 mr-1" />,
          label: status,
        };
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("tr-TR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading)
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="animate-pulse flex flex-col items-center gap-4">
          <Package className="w-12 h-12 text-primary animate-bounce" />
          <p className="text-slate-500 font-medium">
            Siparişleriniz getiriliyor...
          </p>
        </div>
      </div>
    );

  return (
    <div className="min-h-screen bg-slate-50 py-8 px-4 font-sans">
      <div className="container mx-auto max-w-5xl">
        <div className="flex items-center gap-4 mb-8">
          <Button
            variant="outline"
            size="icon"
            className="rounded-full shadow-sm hover:shadow-md"
            onClick={() => router.push("/")}
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-3xl font-extrabold text-slate-800 tracking-tight">
            Sipariş Geçmişim
          </h1>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-4 rounded-xl border border-red-200 text-center font-medium mb-6">
            {error}
          </div>
        )}

        {orders.length === 0 && !error ? (
          <Card className="border-dashed border-2 border-slate-200 bg-transparent shadow-none">
            <CardContent className="flex flex-col items-center justify-center h-64 text-slate-500">
              <Package className="w-16 h-16 text-slate-300 mb-4" />
              <h3 className="text-xl font-bold text-slate-700">
                Henüz hiç siparişiniz yok
              </h3>
              <p className="mt-2 text-sm">
                İlk alışverişinizi yapmak için ürünlere göz atabilirsiniz.
              </p>
              <Button
                className="mt-6 rounded-full"
                onClick={() => router.push("/")}
              >
                Alışverişe Başla
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => {
              const statusConfig = getStatusConfig(order.orderStatus);

              return (
                <Card
                  key={order.id}
                  className="overflow-hidden border-0 shadow-md hover:shadow-lg transition-shadow bg-white rounded-2xl"
                >
                  <div className="bg-slate-100/50 p-5 border-b flex flex-wrap justify-between items-center gap-4">
                    <div>
                      <p className="text-sm font-bold text-slate-500 uppercase tracking-wider mb-1">
                        Sipariş No: #{order.id}
                      </p>
                      <p className="text-sm text-slate-600 font-medium">
                        {formatDate(order.createdAt)}
                      </p>
                    </div>
                    <Badge
                      className={`px-3 py-1.5 flex items-center text-sm ${statusConfig.color} border-0 text-white shadow-sm`}
                    >
                      {statusConfig.icon}
                      {statusConfig.label}
                    </Badge>
                  </div>

                  <CardContent className="p-0">
                    <ul className="divide-y divide-slate-100">
                      {order.items.map((item, index) => (
                        <li
                          key={index}
                          className="flex justify-between items-center p-5 hover:bg-slate-50/50 transition-colors"
                        >
                          <div className="flex items-center gap-4">
                            <div className="w-12 h-12 bg-slate-100 rounded-lg flex items-center justify-center text-slate-400 font-bold">
                              {item.quantity}x
                            </div>
                            <div>
                              <p className="font-bold text-slate-800">
                                {item.productName}
                              </p>
                              <p className="text-sm text-slate-500">
                                Birim Fiyat:{" "}
                                {item.unitPrice.toLocaleString("tr-TR")} ₺
                              </p>
                            </div>
                          </div>
                          <div className="font-black text-slate-700 text-lg">
                            {(item.quantity * item.unitPrice).toLocaleString(
                              "tr-TR",
                            )}{" "}
                            ₺
                          </div>
                        </li>
                      ))}
                    </ul>

                    <div className="bg-slate-50 p-5 border-t flex justify-between items-center">
                      <span className="font-bold text-slate-500 text-lg">
                        Genel Toplam:
                      </span>
                      <span className="text-2xl font-black text-primary">
                        {order.totalAmount.toLocaleString("tr-TR")} ₺
                      </span>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
