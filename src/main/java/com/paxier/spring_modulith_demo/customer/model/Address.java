package com.paxier.spring_modulith_demo.customer.model;

import com.paxier.spring_modulith_demo.customer.entity.AddressType;
import lombok.Builder;

@Builder
public record Address(String street, String city, String zipCode, AddressType type) {}
