package com.paxier.spring_modulith_demo.product;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class ProductsService {

  @ApplicationModuleListener
  void on(OrderPlaceEvent event) throws InterruptedException {
    log.info("Receiving... OrderPlaceEvent for orderId: " + event.orderId());
    Thread.sleep(5000);
    log.info("Received OrderPlaceEvent for orderId: " + event.orderId());
  }
}