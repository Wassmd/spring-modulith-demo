package com.paxier.spring_modulith_demo.product.Repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.*;

import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import com.paxier.spring_modulith_demo.supplier.entity.SupplierEntity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing
@AutoConfigureEmbeddedDatabase(provider = ZONKY, refresh = AFTER_EACH_TEST_METHOD)
class ProductRepositoryTest {

  @Autowired
  ProductRepository repository;

  @Test
  void saveProduct() {
    ProductEntity entity = ProductEntity.builder()
        .name("Test Product")
        .price(9.99)
        .build();

    SupplierEntity supplier = SupplierEntity.builder()
        .supplierName("Test Supplier")
        .productEntity(entity)
        .build();

    entity.getSuppliers().add(supplier);

    var savedEntity = repository.save(entity);
    assertNotNull(savedEntity);
    assertEquals("Test Product", savedEntity.getName());
    assertEquals(9.99, savedEntity.getPrice());

    var fetchedEntity = repository.findById(savedEntity.getProductId());
    assertTrue(fetchedEntity.isPresent());
    assertEquals("Test Product", fetchedEntity.get().getName());
    assertEquals(9.99, fetchedEntity.get().getPrice());

    assertEquals(1, fetchedEntity.get().getSuppliers().size());
    assertEquals("Test Supplier", fetchedEntity.get().getSuppliers().getFirst().getSupplierName());
    assertEquals(1, fetchedEntity.get().getSuppliers().getFirst().getSupplierId());
  }
}
