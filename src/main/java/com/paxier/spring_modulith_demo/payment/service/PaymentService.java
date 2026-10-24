package com.paxier.spring_modulith_demo.payment.service;

import static com.paxier.spring_modulith_demo.payment.model.PaymentStatus.PENDING;
import static com.paxier.spring_modulith_demo.payment.model.PaymentStatus.SUCCESS;

import com.paxier.spring_modulith_demo.payment.model.PaymentStatus;
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
