package com.paxier.spring_modulith_demo.customer.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class Customer {
  private Long id;
  private String name;
  private List<Address> addresses;
}


