package com.paxier.spring_modulith_demo.product;

import com.paxier.spring_modulith_demo.product.internal.ProductRepository;
import com.paxier.spring_modulith_demo.product.internal.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public Product createProduct(Product product) {
    var entity = productRepository.save(productMapper.toEntity(product));
    return productMapper.toDomain(entity);
  }
}
