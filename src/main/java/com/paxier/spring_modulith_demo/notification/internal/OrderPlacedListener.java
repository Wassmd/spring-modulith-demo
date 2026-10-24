package com.paxier.spring_modulith_demo.notification.internal;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class OrderPlacedListener {

  private final NotificationRepository notifications;

  @ApplicationModuleListener // @Async + @Transactional(REQUIRES_NEW) + @TransactionalEventListener
  void on(OrderPlaceEvent event) {
    notifications.enqueue(event.orderId());
    log.info("Notification queued for order {}", event.orderId());
  }
}
