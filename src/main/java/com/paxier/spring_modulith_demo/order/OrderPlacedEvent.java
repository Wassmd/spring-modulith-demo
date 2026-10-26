package com.paxier.spring_modulith_demo.order;
import java.util.Set;
import org.springframework.modulith.events.Externalized;

@Externalized("order-created")
public record OrderPlacedEvent(int orderId, Set<Item> lineItems) {

  public OrderPlacedEvent {
    lineItems = Set.copyOf(lineItems);
  }

  public record Item(Integer id, int productId, int quantity) {
  }
}
