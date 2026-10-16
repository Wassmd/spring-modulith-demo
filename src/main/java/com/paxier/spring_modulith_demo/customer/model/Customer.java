package com.paxier.spring_modulith_demo.customer.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  private Long id;
  private String name;
  private List<Address> addresses;
}


