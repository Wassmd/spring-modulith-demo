package com.paxier.spring_modulith_demo.product.mapper;

import com.paxier.spring_modulith_demo.product.dto.Product;
import com.paxier.spring_modulith_demo.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductEntity toEntity(Product product);
  Product toDomain(ProductEntity productEntity);
}
