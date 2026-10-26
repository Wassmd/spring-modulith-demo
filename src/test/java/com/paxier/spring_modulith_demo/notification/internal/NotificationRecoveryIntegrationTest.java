package com.paxier.spring_modulith_demo.notification.internal;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;

import com.paxier.spring_modulith_demo.order.OrderPlacedEvent;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = {
    "spring.docker.compose.enabled=false",
    "spring.modulith.events.externalization.enabled=false"
})
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)
class NotificationRecoveryIntegrationTest {

  @Autowired ApplicationEventPublisher events;
  @Autowired TransactionTemplate transactions;
  @Autowired JdbcTemplate jdbc;
  @Autowired IncompleteEventPublications publications;
  @MockitoSpyBean NotificationRepository notifications;

  @Test
  void shouldRetainFailedPublicationAndRecoverWithoutDuplicateDelivery() {
    int orderId = 901;
    doThrow(new DataAccessResourceFailureException("Simulated notification database failure"))
        .when(notifications).enqueue(orderId);
    var event = new OrderPlacedEvent(orderId, Set.of(new OrderPlacedEvent.Item(1, 101, 2)));

    transactions.executeWithoutResult(status -> events.publishEvent(event));

    await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
        assertThat(jdbc.queryForObject("""
            SELECT count(*) FROM event_publication
            WHERE listener_id LIKE '%OrderPlacedListener%' AND status = 'FAILED'
              AND CAST(serialized_event AS jsonb) ->> 'orderId' = ?
            """, Long.class, Integer.toString(orderId))).isEqualTo(1));
    assertThat(notifications.findByOrderId(orderId)).isEmpty();

    doCallRealMethod().when(notifications).enqueue(orderId);
    publications.resubmitIncompletePublications(publication ->
        publication.getEvent() instanceof OrderPlacedEvent placed && placed.orderId() == orderId);

    await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
      assertThat(notifications.findByOrderId(orderId)).isPresent();
      assertThat(jdbc.queryForObject("""
          SELECT count(*) FROM event_publication
          WHERE listener_id LIKE '%OrderPlacedListener%' AND completion_date IS NOT NULL
            AND CAST(serialized_event AS jsonb) ->> 'orderId' = ?
          """, Long.class, Integer.toString(orderId))).isEqualTo(1);
    });
    assertThat(jdbc.queryForObject("SELECT count(*) FROM notification.delivery WHERE order_id = ?",
        Long.class, orderId)).isEqualTo(1);
  }
}
