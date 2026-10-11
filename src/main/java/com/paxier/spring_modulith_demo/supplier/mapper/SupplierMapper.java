package com.paxier.spring_modulith_demo.supplier.mapper;

import com.paxier.spring_modulith_demo.supplier.dto.Supplier;
import com.paxier.spring_modulith_demo.supplier.entity.SupplierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
  @Mapping(target = "productId", source = "productEntity.productId")
  Supplier toDomain(SupplierEntity supplierEntity);
}
