package com.paxier.spring_modulith_demo.supplier.repository;

import com.paxier.spring_modulith_demo.supplier.entity.SupplierEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {
  List<SupplierEntity> findByProductEntity_ProductId(long productId);
}
