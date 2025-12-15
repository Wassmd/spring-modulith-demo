package com.paxier.spring_modulith_demo.customer;

import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}

