package com.paxier.spring_modulith_demo.order.internal;

import com.paxier.spring_modulith_demo.order.OrderPlacedEvent;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.paxier.spring_modulith_demo.inventory.InsufficientStockException;
import com.paxier.spring_modulith_demo.inventory.InventoryItem;
import com.paxier.spring_modulith_demo.inventory.InventoryService;
import com.paxier.spring_modulith_demo.notification.NotificationService;
import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import com.paxier.spring_modulith_demo.payment.model.PaymentStatus;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = {
    "spring.docker.compose.enabled=false",
    "spring.modulith.events.externalization.enabled=false"
})
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class OrderWorkflowIntegrationTest {

  @Autowired OrderService orders;
  @Autowired OrderRepository orderRepository;
  @Autowired InventoryService inventory;
  @Autowired NotificationService notifications;
  @Autowired ApplicationEventPublisher events;
  @Autowired TransactionTemplate transactions;
  @Autowired JdbcTemplate jdbc;
  @Autowired MockMvc mvc;
  @MockitoBean PaymentService payment;

  @BeforeEach
  void setUp() {
    orderRepository.deleteAll();
    jdbc.update("DELETE FROM inventory.stock");
    jdbc.update("DELETE FROM notification.delivery");
    when(payment.processPayment(100.0)).thenReturn(PaymentStatus.SUCCESS);
  }

  @Test
  void shouldReserveStockAndQueueNotificationAfterCommit() {
    inventory.addStock(101, 10);
    Order order = order(101, 3);

    transactions.executeWithoutResult(status -> {
      orders.place(order);
      assertThat(inventory.getStock(101).quantity()).isEqualTo(7);
      assertThat(notifications.findByOrderId(order.id())).isEmpty();
    });

    awaitNotification(order.id());
    assertThat(orderRepository.existsById(order.id())).isTrue();
    assertThat(notifications.findByOrderId(order.id())).hasValueSatisfying(notification -> {
      assertThat(notification.status()).isEqualTo("PENDING");
      assertThat(notification.createdAt()).isNotNull();
    });
    await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
        assertThat(jdbc.queryForObject("""
            SELECT count(*) FROM event_publication
            WHERE listener_id LIKE '%OrderPlacedListener%' AND completion_date IS NOT NULL
              AND CAST(serialized_event AS jsonb) ->> 'orderId' = ?
            """, Long.class, order.id().toString())).isEqualTo(1));
  }

  @Test
  void shouldRollBackAllReservationsAndOrderWhenAnyProductIsUnavailable() {
    inventory.addStock(101, 10);
    Order order = new Order(null, Set.of(
        new LineItem(null, 101, 2), new LineItem(null, 202, 1)));

    assertThatThrownBy(() -> orders.place(order)).isInstanceOf(InsufficientStockException.class);

    assertThat(inventory.getStock(101).quantity()).isEqualTo(10);
    assertRolledBack(order.id());
  }

  @Test
  void shouldRestoreStockAndNotNotifyWhenPaymentThrows() {
    inventory.addStock(101, 10);
    Order order = order(101, 3);
    when(payment.processPayment(100.0)).thenThrow(new IllegalStateException("Payment unavailable"));

    assertThatThrownBy(() -> orders.place(order)).hasMessage("Payment unavailable");

    assertThat(inventory.getStock(101).quantity()).isEqualTo(10);
    assertRolledBack(order.id());
  }

  @Test
  void shouldReserveDuplicateProductLines() {
    inventory.addStock(101, 10);
    Order order = new Order(null, Set.of(
        new LineItem(null, 101, 2), new LineItem(null, 101, 3)));

    orders.place(order);
    awaitNotification(order.id());

    assertThat(inventory.getStock(101).quantity()).isEqualTo(5);
  }

  @Test
  void shouldNotOversellUnderConcurrentReservations() throws Exception {
    inventory.addStock(101, 5);
    CountDownLatch start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var first = executor.submit(reserveConcurrently(start));
      var second = executor.submit(reserveConcurrently(start));
      start.countDown();

      assertThat(List.of(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(true, false);
    }
    assertThat(inventory.getStock(101).quantity()).isEqualTo(1);
  }

  @Test
  void shouldDeduplicateReplayedNotifications() {
    var event = new OrderPlacedEvent(600, Set.of(new OrderPlacedEvent.Item(1, 101, 2)));
    transactions.executeWithoutResult(status -> events.publishEvent(event));
    awaitNotification(600);
    transactions.executeWithoutResult(status -> events.publishEvent(event));

    await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
        assertThat(jdbc.queryForObject("""
            SELECT count(*) FROM event_publication
            WHERE listener_id LIKE '%OrderPlacedListener%' AND completion_date IS NOT NULL
              AND CAST(serialized_event AS jsonb) ->> 'orderId' = '600'
            """, Long.class)).isEqualTo(2));
    assertThat(jdbc.queryForObject("SELECT count(*) FROM notification.delivery WHERE order_id = 600",
        Long.class)).isEqualTo(1);
  }

  @Test
  @WithMockUser(roles = "admin")
  void shouldExposeStockAndQueuedNotifications() throws Exception {
    mvc.perform(post("/inventory/101/stock").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantity\":10}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(10));
    Order order = order(101, 3);
    orders.place(order);
    awaitNotification(order.id());

    mvc.perform(get("/inventory/101"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(7));
    mvc.perform(get("/notifications/orders/{orderId}", order.id()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDING"));
    mvc.perform(get("/notifications/orders/999999"))
        .andExpect(status().isNotFound());
    mvc.perform(post("/inventory/101/stock").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantity\":0}"))
        .andExpect(status().isBadRequest());
    mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
            .content("{\"lineItems\":[{\"productId\":101,\"quantity\":0}]}"))
        .andExpect(status().isBadRequest());
    mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
            .content("{\"lineItems\":[{\"productId\":101,\"quantity\":100}]}"))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser(roles = "user")
  @Disabled
  void shouldRequireAdminToAddStock() throws Exception {
    mvc.perform(post("/inventory/101/stock").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantity\":10}"))
        .andExpect(status().isForbidden());
  }

  private Callable<Boolean> reserveConcurrently(CountDownLatch start) {
    return () -> {
      if (!start.await(10, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Concurrent reservation did not start");
      }
      try {
        transactions.executeWithoutResult(status ->
            inventory.reserve(List.of(new InventoryItem(101, 4))));
        return true;
      } catch (InsufficientStockException expected) {
        return false;
      }
    };
  }

  private Order order(int productId, int quantity) {
    return new Order(null, Set.of(new LineItem(null, productId, quantity)));
  }

  private void awaitNotification(int orderId) {
    await().atMost(Duration.ofSeconds(15))
        .until(() -> notifications.findByOrderId(orderId).isPresent());
  }

  private void assertRolledBack(Integer orderId) {
    assertThat(orderRepository.existsById(orderId)).isFalse();
    assertThat(notifications.findByOrderId(orderId)).isEmpty();
    assertThat(jdbc.queryForObject("""
        SELECT count(*) FROM event_publication
        WHERE CAST(serialized_event AS jsonb) ->> 'orderId' = ?
        """, Long.class, orderId.toString())).isZero();
  }
}
