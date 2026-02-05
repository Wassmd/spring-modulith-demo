package com.paxier.spring_modulith_demo.order;

import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class OrderService {
  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher publisher;
  private final PaymentService paymentService;

  void place(Order order) {
    var saved = orderRepository.save(order);
    log.info("Order placed: {}", saved);
    publisher.publishEvent(new OrderPlaceEvent(saved.id(), saved.lineItems()));
    log.info("Payment Status: {}", paymentService.processPayment(100.0));
  }
}
