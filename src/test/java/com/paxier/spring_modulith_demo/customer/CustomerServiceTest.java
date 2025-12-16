package com.paxier.spring_modulith_demo.customer;

import com.paxier.spring_modulith_demo.customer.entity.AddressEntity;
import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import com.paxier.spring_modulith_demo.customer.model.Address;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import com.paxier.spring_modulith_demo.customer.service.CustomerService;
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

  @Mock private CustomerRepository customerRepository;

  @Mock private AddressRepository addressRepository;

  @InjectMocks private CustomerService customerService;

  private CustomerEntity testCustomerEntity;
  private Customer testCustomer =
      Customer.builder()
          .id(1L)
          .name("John Doe")
          .addresses(
              List.of(
                  Address.builder().street("123 Main St").city("New York").zipCode("10001").build()))
          .build();

  private Address testAddress;
  private AddressEntity testAddressEntity = AddressEntity.builder()
      .street("123 Main St")
      .city("New York")
      .zipCode("10001")
      .build();

  @BeforeEach
  void setUp() {
    testAddress =
        Address.builder().street("123 Main St").city("New York").zipCode("10001").build();
    testCustomerEntity = CustomerEntity.builder().name("John Doe").addresses(List.of(testAddressEntity)).build();
  }

  @Test
  void shouldCreateCustomer() {
    // Given
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(testCustomerEntity);

    // When
    Customer result = customerService.createCustomer(testCustomer);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John Doe");
    assertThat(result.getAddresses().getFirst().street()).isEqualTo("123 Main St");
    verify(customerRepository, times(1)).save(testCustomerEntity);
  }

  @Test
  void shouldGetAllCustomers() {
    // Given
    AddressEntity address2 =
        AddressEntity.builder().street("456 Oak Ave").city("Los Angeles").zipCode("90001").build();

    CustomerEntity customer2 = new CustomerEntity(2L, "Jane Smith", List.of(address2));
    List<CustomerEntity> customers = List.of(testCustomerEntity, customer2);

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
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomerEntity));

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
