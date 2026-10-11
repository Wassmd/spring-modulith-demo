package com.paxier.spring_modulith_demo.product.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.paxier.spring_modulith_demo.shared.entity.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product", schema = "product")
public class ProductEntity extends AuditedEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private long productId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private double price;
}
