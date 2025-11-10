package com.paxier.spring_modulith_demo.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new Address(1L, "123 Main St", "New York", "10001");
        testCustomer = new Customer(1L, "John Doe", testAddress);
    }

    @Test
    void shouldCreateCustomer() {
        // Given
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        Customer result = customerService.createCustomer(testCustomer);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getAddress().getStreet()).isEqualTo("123 Main St");
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void shouldGetAllCustomers() {
        // Given
        Address address2 = new Address(2L, "456 Oak Ave", "Los Angeles", "90001");
        Customer customer2 = new Customer(2L, "Jane Smith", address2);
        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // When
        List<Customer> result = customerService.getAllCustomers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCustomerById() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        Customer result = customerService.getCustomerById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found with id: 999");
        verify(customerRepository, times(1)).findById(999L);
    }
}

