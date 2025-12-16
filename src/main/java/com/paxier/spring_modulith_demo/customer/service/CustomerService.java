package com.paxier.spring_modulith_demo.customer.service;

import com.paxier.spring_modulith_demo.customer.CustomerRepository;
import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import com.paxier.spring_modulith_demo.customer.mapper.CustomerMapper;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Transactional
  public Customer createCustomer(Customer customer) {
    // Save address first (though cascade will handle this)
    var customerEntity = customerMapper.toEntity(customer);

    CustomerEntity entity = customerRepository.save(customerEntity);

    return customerMapper.toModel(entity);
  }

  @Transactional(readOnly = true)
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll().stream().map(customerMapper::toModel).toList();
  }

  @Transactional(readOnly = true)
  public Customer getCustomerById(Long id) {
    return customerMapper.toModel(
        customerRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id)));
  }
}
