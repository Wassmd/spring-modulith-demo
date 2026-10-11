package com.paxier.spring_modulith_demo.customer;

import com.paxier.spring_modulith_demo.customer.entity.AddressEntity;
import com.paxier.spring_modulith_demo.customer.entity.AddressType;
import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD, provider = EMBEDDED)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldSaveCustomerWithoutAddress() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("John Doe")
                .addresses(new ArrayList<>())
                .build();

        // When
        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();

        // Then
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("John Doe");
        assertThat(savedCustomer.getAddresses()).isEmpty();
    }

    @Test
    void shouldSaveCustomerWithSingleAddress() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("Jane Smith")
                .addresses(new ArrayList<>())
                .build();

        AddressEntity address = AddressEntity.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .type(AddressType.HOME)
                .customerEntity(customer)
                .build();

        customer.getAddresses().add(address);

        // When
        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CustomerEntity> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getName()).isEqualTo("Jane Smith");
        assertThat(foundCustomer.get().getAddresses()).hasSize(1);
        assertThat(foundCustomer.get().getAddresses().get(0).getStreet()).isEqualTo("123 Main St");
        assertThat(foundCustomer.get().getAddresses().get(0).getCity()).isEqualTo("New York");
        assertThat(foundCustomer.get().getAddresses().get(0).getZipCode()).isEqualTo("10001");
        assertThat(foundCustomer.get().getAddresses().get(0).getType()).isEqualTo(AddressType.HOME);
    }

    @Test
    void shouldSaveCustomerWithMultipleAddresses() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("Bob Johnson")
                .addresses(new ArrayList<>())
                .build();

        AddressEntity homeAddress = AddressEntity.builder()
                .street("456 Oak Ave")
                .city("Boston")
                .zipCode("02101")
                .type(AddressType.HOME)
                .customerEntity(customer)
                .build();

        AddressEntity workAddress = AddressEntity.builder()
                .street("789 Business Blvd")
                .city("Boston")
                .zipCode("02102")
                .type(AddressType.WORK)
                .customerEntity(customer)
                .build();

        customer.getAddresses().add(homeAddress);
        customer.getAddresses().add(workAddress);

        // When
        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CustomerEntity> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getAddresses()).hasSize(2);
        assertThat(foundCustomer.get().getAddresses())
                .extracting(AddressEntity::getType)
                .containsExactlyInAnyOrder(AddressType.HOME, AddressType.WORK);
    }

    @Test
    void shouldDeleteCustomerAndCascadeDeleteAddresses() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("Alice Williams")
                .addresses(new ArrayList<>())
                .build();

        AddressEntity address = AddressEntity.builder()
                .street("321 Park St")
                .city("Chicago")
                .zipCode("60601")
                .type(AddressType.HOME)
                .customerEntity(customer)
                .build();

        customer.getAddresses().add(address);
        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();
        Long customerId = savedCustomer.getId();

        // When
        customerRepository.deleteById(customerId);
        entityManager.flush();

        // Then
        Optional<CustomerEntity> deletedCustomer = customerRepository.findById(customerId);
        assertThat(deletedCustomer).isEmpty();
    }

    @Test
    void shouldUpdateCustomerName() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("Old Name")
                .addresses(new ArrayList<>())
                .build();

        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<CustomerEntity> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();

        CustomerEntity updatedCustomer = CustomerEntity.builder()
                .id(foundCustomer.get().getId())
                .name("New Name")
                .addresses(foundCustomer.get().getAddresses())
                .build();

        customerRepository.save(updatedCustomer);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CustomerEntity> finalCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(finalCustomer).isPresent();
        assertThat(finalCustomer.get().getName()).isEqualTo("New Name");
    }

    @Test
    void shouldFindAllCustomers() {
        // Given
        CustomerEntity customer1 = CustomerEntity.builder()
                .name("Customer 1")
                .addresses(new ArrayList<>())
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .name("Customer 2")
                .addresses(new ArrayList<>())
                .build();

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        entityManager.flush();

        // When
        List<CustomerEntity> customers = customerRepository.findAll();

        // Then
        assertThat(customers).hasSize(2);
        assertThat(customers)
                .extracting(CustomerEntity::getName)
                .containsExactlyInAnyOrder("Customer 1", "Customer 2");
    }

    @Test
    void shouldRemoveAddressFromCustomerWithOrphanRemoval() {
        // Given
        CustomerEntity customer = CustomerEntity.builder()
                .name("Customer With Addresses")
                .addresses(new ArrayList<>())
                .build();

        AddressEntity address1 = AddressEntity.builder()
                .street("Address 1")
                .city("City 1")
                .zipCode("10001")
                .type(AddressType.HOME)
                .customerEntity(customer)
                .build();

        AddressEntity address2 = AddressEntity.builder()
                .street("Address 2")
                .city("City 2")
                .zipCode("10002")
                .type(AddressType.WORK)
                .customerEntity(customer)
                .build();

        customer.getAddresses().add(address1);
        customer.getAddresses().add(address2);

        CustomerEntity savedCustomer = customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        // When - Remove one address
        Optional<CustomerEntity> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();
        foundCustomer.get().getAddresses().remove(0);
        customerRepository.save(foundCustomer.get());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CustomerEntity> updatedCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(updatedCustomer).isPresent();
        assertThat(updatedCustomer.get().getAddresses()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        // When
        Optional<CustomerEntity> customer = customerRepository.findById(999L);

        // Then
        assertThat(customer).isEmpty();
    }
}
