package com.paxier.spring_modulith_demo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Customer testCustomer;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new Address(1L, "123 Main St", "New York", "10001");
        testCustomer = new Customer(1L, "John Doe", testAddress);
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        // Given
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"))
                .andExpect(jsonPath("$.address.city").value("New York"))
                .andExpect(jsonPath("$.address.zipCode").value("10001"));
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        // Given
        Address address2 = new Address(2L, "456 Oak Ave", "Los Angeles", "90001");
        Customer customer2 = new Customer(2L, "Jane Smith", address2);
        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

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
        when(customerService.getCustomerById(eq(1L))).thenReturn(testCustomer);

        // When & Then
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address.street").value("123 Main St"));
    }
}

