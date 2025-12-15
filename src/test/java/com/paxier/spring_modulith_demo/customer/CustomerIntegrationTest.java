package com.paxier.spring_modulith_demo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;
  Address address1 =
      Address.builder().street("123 Main St").city("New York").zipCode("10001").build();
  Address address2 =
      Address.builder().street("456 Oak Ave").city("Los Angeles").zipCode("90001").build();
    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    void shouldCreateCustomerWithAddress() throws Exception {
        // Given
      Address address =
          Address.builder().street("123 Main St").city("New York").zipCode("10001").build();
        Customer customer = new Customer(null, "John Doe", address);

        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"))
                .andExpect(jsonPath("$.address.city").value("New York"))
                .andExpect(jsonPath("$.address.zipCode").value("10001"));
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        // Given
        Customer customer1 = new Customer(null, "John Doe", address1);
        customerRepository.save(customer1);


        Customer customer2 = new Customer(null, "Jane Smith", address2);
        customerRepository.save(customer2);

        // When & Then
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        // Given
        Customer customer = new Customer(null, "John Doe", address1);
        Customer savedCustomer = customerRepository.save(customer);

        // When & Then
        mockMvc.perform(get("/customers/" + savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"));
    }

    @Test
    void shouldSaveAddressInSeparateTable() throws Exception {
        // Given
        Customer customer = new Customer(null, "John Doe", address1);

        // When
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());

        // Then - Verify both tables have data
        long customerCount = customerRepository.count();
        long addressCount = addressRepository.count();

        assert customerCount == 1 : "Expected 1 customer, but found " + customerCount;
        assert addressCount == 1 : "Expected 1 address, but found " + addressCount;
    }
}

