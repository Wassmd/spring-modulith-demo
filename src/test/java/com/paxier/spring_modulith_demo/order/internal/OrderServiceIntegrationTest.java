package com.paxier.spring_modulith_demo.order.internal;

import com.paxier.spring_modulith_demo.order.OrderPlacedEvent;

import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import com.paxier.spring_modulith_demo.payment.model.PaymentStatus;
import com.paxier.spring_modulith_demo.inventory.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import java.util.Set;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.modulith.events.externalization.enabled=false")
@RecordApplicationEvents
@Transactional
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class OrderServiceIntegrationTest {

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ApplicationEvents applicationEvents;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @MockitoBean
  private PaymentService paymentService;

  @Autowired
  private InventoryService inventoryService;

  @BeforeEach
  void setUp() {
    // Clean up before each test
    orderRepository.deleteAll();
    inventoryService.addStock(100, 100);
    inventoryService.addStock(200, 100);
    inventoryService.addStock(300, 100);
  }

  @Test
  void shouldSaveOrderToDatabase() {
    // Given
    LineItem lineItem1 = new LineItem(null, 100, 2);
    LineItem lineItem2 = new LineItem(null, 200, 3);
    Order order = new Order(null, Set.of(lineItem1, lineItem2));

    // When
    orderService.place(order);

    // Then
    Iterable<Order> orders = orderRepository.findAll();
    assertThat(orders).hasSize(1);
    Order savedOrder = orders.iterator().next();
    assertThat(savedOrder.id()).isNotNull();
    assertThat(savedOrder.lineItems()).hasSize(2);
  }

  @Test
  void shouldPublishOrderPlacedEventAfterSavingOrder() {
    // Given
    LineItem lineItem = new LineItem(null, 100, 5);
    Order order = new Order(null, Set.of(lineItem));

    // When
    orderService.place(order);

    // Then - Verify event was published
    long eventCount = applicationEvents.stream(OrderPlacedEvent.class).count();
    assertThat(eventCount).isEqualTo(1);

    OrderPlacedEvent publishedEvent = applicationEvents.stream(OrderPlacedEvent.class)
        .findFirst()
        .orElseThrow();

    assertThat(publishedEvent.orderId()).isNotNull();
    assertThat(publishedEvent.lineItems()).hasSize(1);
  }

  @Test
  void shouldSaveMultipleOrders() {
    // Given
    Order order1 = new Order(null, Set.of(new LineItem(null, 100, 2)));
    Order order2 = new Order(null, Set.of(new LineItem(null, 200, 3)));
    Order order3 = new Order(null, Set.of(new LineItem(null, 300, 1)));

    // When
    orderService.place(order1);
    orderService.place(order2);
    orderService.place(order3);

    // Then
    Iterable<Order> orders = orderRepository.findAll();
    assertThat(orders).hasSize(3);

    // Verify events were published for all orders
    long eventCount = applicationEvents.stream(OrderPlacedEvent.class).count();
    assertThat(eventCount).isEqualTo(3);
  }

  @Test
  void shouldSaveOrderWithMultipleLineItems() {
    // Given
    LineItem lineItem1 = new LineItem(null, 100, 1);
    LineItem lineItem2 = new LineItem(null, 200, 2);
    LineItem lineItem3 = new LineItem(null, 300, 3);
    Order order = new Order(null, Set.of(lineItem1, lineItem2, lineItem3));

    // When
    orderService.place(order);

    // Then
    Iterable<Order> orders = orderRepository.findAll();
    Order savedOrder = orders.iterator().next();
    assertThat(savedOrder.lineItems()).hasSize(3);

    // Verify the event contains all line items
    OrderPlacedEvent publishedEvent = applicationEvents.stream(OrderPlacedEvent.class)
        .findFirst()
        .orElseThrow();
    assertThat(publishedEvent.lineItems()).hasSize(3);
  }

  @Test
  void shouldGenerateOrderIdAutomatically() {
    // Given
    Order order1 = new Order(null, Set.of(new LineItem(null, 100, 1)));
    Order order2 = new Order(null, Set.of(new LineItem(null, 200, 2)));

    // When
    orderService.place(order1);
    orderService.place(order2);

    // Then - Verify both orders have different IDs
    Iterable<Order> orders = orderRepository.findAll();
    assertThat(orders).hasSize(2);

    var orderIterator = orders.iterator();
    Order savedOrder1 = orderIterator.next();
    Order savedOrder2 = orderIterator.next();

    assertThat(savedOrder1.id()).isNotNull();
    assertThat(savedOrder2.id()).isNotNull();
    assertThat(savedOrder1.id()).isNotEqualTo(savedOrder2.id());
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void shouldCommitOrderAndEventPublicationWithoutTestTransaction() {
    Order order = new Order(null, Set.of(new LineItem(null, 100, 2)));
    when(paymentService.processPayment(100.0)).thenReturn(PaymentStatus.SUCCESS);

    orderService.place(order);

    assertThat(orderRepository.existsById(order.id())).isTrue();
    assertThat(eventPublicationCount(order.id())).isPositive();
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void shouldRollBackOrderAndEventPublicationWhenPaymentFails() {
    Order order = new Order(null, Set.of(new LineItem(null, 100, 2)));
    IllegalStateException failure = new IllegalStateException("Payment unavailable");
    when(paymentService.processPayment(100.0)).thenAnswer(invocation -> {
      assertThat(orderRepository.existsById(order.id())).isTrue();
      assertThat(eventPublicationCount(order.id())).isPositive();
      throw failure;
    });

    assertThatThrownBy(() -> orderService.place(order)).isSameAs(failure);

    assertThat(orderRepository.existsById(order.id())).isFalse();
    assertThat(eventPublicationCount(order.id())).isZero();
  }

  private Long eventPublicationCount(Integer orderId) {
    return jdbcTemplate.queryForObject("""
        SELECT count(*) FROM event_publication
        WHERE event_type = ? AND CAST(serialized_event AS jsonb) ->> 'orderId' = ?
        """, Long.class, OrderPlacedEvent.class.getName(), orderId.toString());
  }
}
