package com.paxier.spring_modulith_demo.customer;

import tools.jackson.databind.ObjectMapper;
import com.paxier.spring_modulith_demo.customer.controller.CustomerController;
import com.paxier.spring_modulith_demo.customer.model.Address;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import com.paxier.spring_modulith_demo.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.paxier.spring_modulith_demo.customer.entity.AddressType.HOME;
import static com.paxier.spring_modulith_demo.customer.entity.AddressType.WORK;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean
  private CustomerService customerService;

  private Customer testCustomer;

  @BeforeEach
  void setUp() {
    Address testAddress = Address.builder().street("123 Main St").city("New York").zipCode("10001").type(HOME).build();
    testCustomer = Customer.builder().id(1L).name("John Doe").addresses(List.of(testAddress)).build() ;
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
        .andExpect(jsonPath("$.addresses[0].street").value("123 Main St"))
        .andExpect(jsonPath("$.addresses[0].city").value("New York"))
        .andExpect(jsonPath("$.addresses[0].zipCode").value("10001"))
        .andExpect(jsonPath("$.addresses[0].type").value("HOME"));
  }

  @Test
  void shouldGetAllCustomers() throws Exception {
    // Given
    Address address2 =
        Address.builder()
            .street("456 Oak Ave")
            .city("Los Angeles")
            .zipCode("90001")
            .type(WORK)
            .build();
    Customer customer2 =  Customer.builder().name("Jane Smith").addresses(List.of(address2)).build();

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
  void shouldGetCustomerById() throws Exception {
    // Given
    when(customerService.getCustomerById(eq(1L))).thenReturn(testCustomer);

    // When & Then
    mockMvc
        .perform(get("/customers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.addresses[0].street").value("123 Main St"));
  }
}
