package com.paxier.spring_modulith_demo.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class OrderService {
  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher publisher;

  void place(Order order) {
    var saved = orderRepository.save(order);
    System.out.println("Order placed: " + saved);
    publisher.publishEvent(new OrderPlaceEvent(saved.orderId()));
  }
}
