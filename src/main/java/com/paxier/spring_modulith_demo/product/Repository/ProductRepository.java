package com.paxier.spring_modulith_demo.product.Repository;

import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
}
