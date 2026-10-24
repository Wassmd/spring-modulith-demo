package com.paxier.spring_modulith_demo.notification;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

@ApplicationModuleTest
@TestPropertySource(properties = {
    "spring.docker.compose.enabled=false",
    "spring.modulith.events.externalization.enabled=false"
})
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class NotificationModuleTest {

  @Autowired NotificationService notifications;
  @Autowired JdbcTemplate jdbc;

  @Test
  void queuesNotificationWhenOrderIsPlaced(Scenario scenario) {
    int orderId = 951;

    scenario.publish(new OrderPlaceEvent(orderId, Set.of(new OrderPlaceEvent.Item(1, 101, 2))))
        .andCleanup(() -> jdbc.update("DELETE FROM notification.delivery WHERE order_id = ?", orderId))
        .andWaitForStateChange(() -> notifications.findByOrderId(orderId), Optional::isPresent)
        .andVerify(queued -> assertThat(queued).hasValueSatisfying(n -> assertThat(n.status()).isEqualTo("PENDING")));
  }
}
