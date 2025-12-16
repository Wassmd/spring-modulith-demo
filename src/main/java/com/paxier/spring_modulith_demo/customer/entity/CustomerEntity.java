package com.paxier.spring_modulith_demo.customer.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "customer", schema = "customer")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "addresses")
public class CustomerEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AddressEntity> addresses = new ArrayList<>();
}
