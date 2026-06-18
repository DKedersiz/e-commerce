package com.dogukan.ecommerce.scheduler;

import com.dogukan.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredOrders() {
        log.info("Süresi dolmuş siparişler kontrol ediliyor...");
        orderService.cancelExpiredOrders();
    }
}
