package com.paxier.spring_modulith_demo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paxier.spring_modulith_demo.customer.model.Address;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private CustomerRepository customerRepository;

  @BeforeEach
  void setUp() {
    customerRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "admin")
  void shouldCreateCustomerWithAddress() throws Exception {
    // Given
    Address address =
        Address.builder().street("123 Main St").city("New York").zipCode("10001").build();
    Customer customer = Customer.builder().name("John Doe").addresses(List.of(address)).build();

    // When & Then
    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.addresses[0].street").value("123 Main St"))
        .andExpect(jsonPath("$.addresses[0].city").value("New York"))
        .andExpect(jsonPath("$.addresses[0].zipCode").value("10001"));
  }
}
