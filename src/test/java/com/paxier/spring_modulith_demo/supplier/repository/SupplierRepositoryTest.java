package com.paxier.spring_modulith_demo.supplier.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import com.paxier.spring_modulith_demo.supplier.entity.SupplierEntity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED, refresh = AFTER_EACH_TEST_METHOD)
class SupplierRepositoryTest {

  @Autowired
  SupplierRepository repository;

  @Autowired
  TestEntityManager entityManager;

  @Test
  void findSuppliersByProductId() {
    ProductEntity product = ProductEntity.builder()
        .name("Test Product")
        .price(9.99)
        .build();
    entityManager.persist(product);

    SupplierEntity supplier = SupplierEntity.builder()
        .supplierName("Test Supplier")
        .productEntity(product)
        .build();
    entityManager.persist(supplier);
    entityManager.flush();

    List<SupplierEntity> suppliers = repository.findByProductEntity_ProductId(product.getProductId());

    assertEquals(1, suppliers.size());
    assertEquals("Test Supplier", suppliers.getFirst().getSupplierName());
    assertEquals(product.getProductId(), suppliers.getFirst().getProductEntity().getProductId());
  }
}
