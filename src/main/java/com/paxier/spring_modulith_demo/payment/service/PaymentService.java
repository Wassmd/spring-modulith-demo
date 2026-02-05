package com.paxier.spring_modulith_demo.payment.service;

import static com.paxier.spring_modulith_demo.payment.service.PaymentStatus.PENDING;
import static com.paxier.spring_modulith_demo.payment.service.PaymentStatus.SUCCESS;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  public PaymentStatus processPayment(double amount) {
    // Dummy implementation for payment processing
    if (amount > 0) {
      return SUCCESS;
    } else {
      return PENDING;
    }
  }
}
