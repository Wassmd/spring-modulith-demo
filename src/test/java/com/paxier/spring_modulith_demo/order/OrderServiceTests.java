package com.paxier.spring_modulith_demo.order;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private OrderService orderService;

  @Test
  void placeOrder_ShouldSaveOrderAndPublishEvent() {
    //given
    Order order = new Order(1, Set.of(new LineItem(1, 2, 3)));
    when(orderRepository.save(order)).thenReturn(order);

    //when
    orderService.place(order);

    //then
    verify(orderRepository, times(1)).save(order);
  }
}
