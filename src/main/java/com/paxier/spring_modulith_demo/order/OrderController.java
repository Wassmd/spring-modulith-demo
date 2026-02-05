package com.paxier.spring_modulith_demo.order;

import com.paxier.spring_modulith_demo.customer.model.Customer;
import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
class OrderController {
  private final OrderService orderService;

  @PostMapping
  void place(@Valid @RequestBody Order order, Customer customer) {
    orderService.place(order);
  }
}