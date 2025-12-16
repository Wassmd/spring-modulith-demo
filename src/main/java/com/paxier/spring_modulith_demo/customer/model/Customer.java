package com.paxier.spring_modulith_demo.customer.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
  private Long id;
  private String name;
  private List<Address> addresses;
}


