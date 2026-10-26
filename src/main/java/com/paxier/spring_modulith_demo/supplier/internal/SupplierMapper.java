package com.paxier.spring_modulith_demo.supplier.internal;

import com.paxier.spring_modulith_demo.supplier.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
  @Mapping(target = "productId", source = "productEntity.productId")
  Supplier toDomain(SupplierEntity supplierEntity);
}
