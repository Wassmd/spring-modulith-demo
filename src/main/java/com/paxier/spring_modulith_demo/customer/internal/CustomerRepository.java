package com.paxier.spring_modulith_demo.customer.internal;

import com.paxier.spring_modulith_demo.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}

