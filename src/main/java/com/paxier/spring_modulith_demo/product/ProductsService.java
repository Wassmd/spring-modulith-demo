package com.paxier.spring_modulith_demo.product;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class ProductsService {

  @ApplicationModuleListener
  void on(OrderPlaceEvent event) throws InterruptedException {
    System.out.println("Receiving... OrderPlaceEvent for orderId: " + event.orderId());
    Thread.sleep(5000);
    System.out.println("Received OrderPlaceEvent for orderId: " + event.orderId());

  }
}