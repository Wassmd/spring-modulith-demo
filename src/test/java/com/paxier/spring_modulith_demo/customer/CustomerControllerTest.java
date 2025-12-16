package com.paxier.spring_modulith_demo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paxier.spring_modulith_demo.customer.controller.CustomerController;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import com.paxier.spring_modulith_demo.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.paxier.spring_modulith_demo.customer.AddressType.WORK;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean
  private CustomerService customerService;

  private Customer testCustomer;

  @BeforeEach
  void setUp() {
    Address testAddress = Address.builder().street("123 Main St").city("New York").zipCode("10001").build();
    testCustomer = new Customer(1L, "John Doe", testAddress);
  }

  @Test
  @WithMockUser(roles = "admin")
  void shouldCreateCustomer() throws Exception {
    // Given
    when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

    // When & Then
    mockMvc
        .perform(
            post("/customers")
                .with(csrf()) //the test context still enforces CSRF, so the POST is 403 even though it\’s disabled globally in the main app.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.address.street").value("123 Main St"))
        .andExpect(jsonPath("$.address.city").value("New York"))
        .andExpect(jsonPath("$.address.zipCode").value("10001"))
        .andExpect(jsonPath("$.address.type").value("HOME"));
  }

  @Test
  @WithMockUser
  void shouldGetAllCustomers() throws Exception {
    // Given
    Address address2 =
        Address.builder()
            .street("456 Oak Ave")
            .city("Los Angeles")
            .zipCode("90001")
            .type(WORK)
            .build();
    Customer customer2 = new Customer(2L, "Jane Smith", address2);
    List<Customer> customers = Arrays.asList(testCustomer, customer2);
    when(customerService.getAllCustomers()).thenReturn(customers);

    // When & Then
    mockMvc
        .perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[1].name").value("Jane Smith"));
  }

  @Test
  @WithMockUser
  void shouldGetCustomerById() throws Exception {
    // Given
    when(customerService.getCustomerById(eq(1L))).thenReturn(testCustomer);

    // When & Then
    mockMvc
        .perform(get("/customers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.address.street").value("123 Main St"));
  }
}
