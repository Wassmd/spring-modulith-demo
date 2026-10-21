package com.paxier.spring_modulith_demo.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.paxier.spring_modulith_demo.inventory.InsufficientStockException;
import com.paxier.spring_modulith_demo.inventory.InventoryItem;
import com.paxier.spring_modulith_demo.inventory.InventoryService;
import com.paxier.spring_modulith_demo.payment.service.PaymentService;
import com.paxier.spring_modulith_demo.payment.service.PaymentStatus;
import java.util.Set;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PaymentService paymentService;

  @Mock
  private InventoryService inventoryService;

  @Mock
  ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private OrderService orderService;

  @Test
  void placeOrder_ShouldSaveOrderAndPublishEvent() {
    //given
    Order order = new Order(1, Set.of(new LineItem(1, 2, 3)));
    when(orderRepository.save(order)).thenReturn(order);
    when(paymentService.processPayment(any(Double.class))).thenReturn(PaymentStatus.SUCCESS);
    //when
    orderService.place(order);

    //then
    verify(orderRepository, times(1)).save(order);
    verify(inventoryService).reserve(List.of(new InventoryItem(2, 3)));
    ArgumentCaptor<OrderPlaceEvent> event = ArgumentCaptor.forClass(OrderPlaceEvent.class);
    verify(applicationEventPublisher).publishEvent(event.capture());
    assertThat(event.getValue().lineItems()).containsExactly(new OrderPlaceEvent.Item(1, 2, 3));
    order.lineItems().iterator().next().quantity(99);
    assertThat(event.getValue().lineItems().iterator().next().quantity()).isEqualTo(3);
  }

  @Test
  void shouldNotPublishOrPayWhenStockIsInsufficient() {
    Order order = new Order(1, Set.of(new LineItem(1, 2, 3)));
    when(orderRepository.save(order)).thenReturn(order);
    doThrow(new InsufficientStockException(2, 3))
        .when(inventoryService).reserve(List.of(new InventoryItem(2, 3)));

    assertThatThrownBy(() -> orderService.place(order)).isInstanceOf(InsufficientStockException.class);

    verify(applicationEventPublisher, never()).publishEvent(any(OrderPlaceEvent.class));
    verify(paymentService, never()).processPayment(any(Double.class));
  }
}
