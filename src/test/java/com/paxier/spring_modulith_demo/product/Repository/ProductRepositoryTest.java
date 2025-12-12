package com.paxier.spring_modulith_demo.product.Repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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

    var savedEntity = repository.save(entity);
    assertNotNull(savedEntity);
    assertEquals("Test Product", savedEntity.getName());
    assertEquals(9.99, savedEntity.getPrice());
  }
}
