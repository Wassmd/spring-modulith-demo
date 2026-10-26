package com.paxier.spring_modulith_demo.order.internal;

import com.paxier.spring_modulith_demo.order.OrderPlacedEvent;

import static java.util.stream.Collectors.toSet;

import com.paxier.spring_modulith_demo.inventory.InventoryItem;
import com.paxier.spring_modulith_demo.inventory.InventoryService;
import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderService {
  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher publisher;
  private final PaymentService paymentService;
  private final InventoryService inventoryService;

  @Transactional
  void place(Order order) {
    var saved = orderRepository.save(order);
    var items = saved.lineItems();

    // 1. Synchronous call to the Inventory module (same transaction)
    inventoryService.reserve(items.stream()
        .map(item -> new InventoryItem(item.productId(), item.quantity()))
        .toList());

    // 2. Event for asynchronous listeners (delivered after commit)
    publisher.publishEvent(new OrderPlacedEvent(saved.id(), items.stream()
        .map(item -> new OrderPlacedEvent.Item(item.id(), item.productId(), item.quantity()))
        .collect(toSet())));

    log.info("Order placed: {}, payment: {}", saved.id(), paymentService.processPayment(100.0));
  }
}
