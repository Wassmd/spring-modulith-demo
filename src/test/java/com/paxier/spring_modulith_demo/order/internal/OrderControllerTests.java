package com.paxier.spring_modulith_demo.order.internal;

import tools.jackson.databind.ObjectMapper;
import com.paxier.spring_modulith_demo.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import(SecurityConfig.class)
@WithMockUser
public class OrderControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private OrderService orderService;

  @Test
  void shouldPlaceOrderSuccessfully() throws Exception {
    // Given
    LineItem lineItem1 = new LineItem(1, 100, 2);
    LineItem lineItem2 = new LineItem(2, 200, 1);
    Order order = new Order(1, Set.of(lineItem1, lineItem2));

    doNothing().when(orderService).place(any(Order.class));

    // When & Then
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(order)))
        .andExpect(status().isOk());

    verify(orderService, times(1)).place(any(Order.class));
  }

  @Test
  void shouldReturnBadRequestWhenOrderIsInvalid() throws Exception {
    // Given - invalid JSON
    String invalidJson = "{invalid}";

    // When & Then
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldPlaceOrderWithEmptyLineItems() throws Exception {
    // Given
    Order order = new Order(1, Set.of());

    doNothing().when(orderService).place(any(Order.class));

    // When & Then
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(order)))
        .andExpect(status().isBadRequest());

    verify(orderService, times(0)).place(any(Order.class));
  }
}
