package com.paxier.spring_modulith_demo.order;

import com.paxier.spring_modulith_demo.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {
  private final OrderService orderService;

  @PostMapping
  void place(@RequestBody Order order, Customer customer) {
    orderService.place(order);
  }
}