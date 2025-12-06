package com.paxier.spring_modulith_demo.product.service;

import com.paxier.spring_modulith_demo.product.Repository.ProductRepository;
import com.paxier.spring_modulith_demo.product.dto.Product;
import com.paxier.spring_modulith_demo.product.mapper.ProductMapper;
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
