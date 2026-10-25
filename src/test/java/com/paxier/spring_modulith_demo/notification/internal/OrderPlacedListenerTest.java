package com.paxier.spring_modulith_demo.notification.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderPlacedListenerTest {

  @Test
  void enqueuesNotificationForPlacedOrder() {
    var repo = mock(NotificationRepository.class);

    new OrderPlacedListener(repo).on(new OrderPlaceEvent(951, Set.of()));

    verify(repo).enqueue(951);
  }
}
