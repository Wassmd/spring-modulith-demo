package com.paxier.spring_modulith_demo.product.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.paxier.spring_modulith_demo.shared.entity.AuditedEntity;
import com.paxier.spring_modulith_demo.supplier.entity.SupplierEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
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

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "productEntity", orphanRemoval = true, cascade = ALL)
  private List<SupplierEntity> suppliers = new ArrayList<>();
}
