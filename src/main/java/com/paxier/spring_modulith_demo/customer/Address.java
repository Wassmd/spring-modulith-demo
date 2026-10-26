package com.paxier.spring_modulith_demo.customer;

import lombok.Builder;

@Builder
public record Address(
    String street,
    String city,
    String zipCode,
    AddressType type) {}
