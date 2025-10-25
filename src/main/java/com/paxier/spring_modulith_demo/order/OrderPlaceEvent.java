package com.paxier.spring_modulith_demo.order;
import java.util.Set;
import org.springframework.modulith.events.Externalized;

@Externalized("order-created")
public record OrderPlaceEvent(int orderId, Set<LineItem> lineItems)  {
}

