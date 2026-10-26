package com.paxier.spring_modulith_demo.supplier.internal;

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
@Table(name = "supplier", schema = "supplier")
public class SupplierEntity extends AuditedEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private long supplierId;

  private String supplierName;

  // Product is referenced by ID only; the entity stays inside the Product module.
  @Column(name = "product_id")
  private Long productId;
}
