package com.paxier.spring_modulith_demo.product.entity;

import com.paxier.spring_modulith_demo.shared.entity.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "product", schema = "product")
public class ProductEntity extends AuditedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long productId;

  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private Double price;
}
