package com.paxier.spring_modulith_demo.supplier.internal;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
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
    long productId = 42L;
    entityManager.persist(SupplierEntity.builder()
        .supplierName("Test Supplier")
        .productId(productId)
        .build());
    entityManager.persist(SupplierEntity.builder()
        .supplierName("Other Supplier")
        .productId(7L)
        .build());
    entityManager.flush();

    List<SupplierEntity> suppliers = repository.findByProductId(productId);

    assertEquals(1, suppliers.size());
    assertEquals("Test Supplier", suppliers.getFirst().getSupplierName());
    assertEquals(productId, suppliers.getFirst().getProductId());
  }
}
