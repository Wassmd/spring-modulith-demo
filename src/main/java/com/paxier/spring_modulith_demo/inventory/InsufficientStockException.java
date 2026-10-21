package com.paxier.spring_modulith_demo.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(int productId, int quantity) {
    super("Insufficient stock for product " + productId + ": requested " + quantity);
  }
}
