package com.paxier.spring_modulith_demo.notification;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;

import com.paxier.spring_modulith_demo.order.OrderPlaceEvent;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

@ApplicationModuleTest
@TestPropertySource(properties = {
    "spring.docker.compose.enabled=false",
    "spring.modulith.events.externalization.enabled=false"
})
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)
class NotificationModuleTest {

  @Autowired NotificationService notifications;

  @Test
  void queuesNotificationWhenOrderIsPlaced(Scenario scenario) {
    var event = new OrderPlaceEvent(951, Set.of(new OrderPlaceEvent.Item(1, 101, 2)));

    scenario.publish(event)
        .andWaitForStateChange(() -> notifications.findByOrderId(951))
        .andVerify(notification -> assertThat(notification).get()
            .extracting(Notification::status).isEqualTo("PENDING"));
  }
}
