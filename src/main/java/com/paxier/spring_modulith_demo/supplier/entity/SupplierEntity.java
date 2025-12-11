package com.paxier.spring_modulith_demo.supplier.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import com.paxier.spring_modulith_demo.shared.entity.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "supplier", schema = "supplier")
public class SupplierEntity extends AuditedEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private long supplierId;
  private String supplierName;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "product_Id")
  private ProductEntity productEntity;
}
