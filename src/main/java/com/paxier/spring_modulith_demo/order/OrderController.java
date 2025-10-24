package com.paxier.spring_modulith_demo.order;

import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
class OrderController {
  private final Orders orders;

  OrderController(Orders orders) {
    this.orders = orders;
  }

  @PostMapping
  void place(@RequestBody Order order) {
    orders.place(order);
  }
}

@Service
@Transactional
class Orders {
  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher publisher;

  Orders(OrderRepository orderRepository, ApplicationEventPublisher publisher) {
    this.orderRepository = orderRepository;
    this.publisher = publisher;
  }

  void place(Order order) {
    var saved = orderRepository.save(order);
    System.out.println("Order placed: " + saved);
    publisher.publishEvent(new OrderPlaceEvent(saved.orderId()));
  }
}

interface OrderRepository extends ListCrudRepository<Order, Integer> {
}

@Table("orders")
record Order(@Id Integer orderId, Set<LineItem> lineItems) {
}

@Table("orders_line_items")
record LineItem(@Id Integer id, int product, int quantity) {
}