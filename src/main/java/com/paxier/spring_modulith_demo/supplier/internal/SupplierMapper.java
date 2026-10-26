package com.paxier.spring_modulith_demo.supplier.internal;

import com.paxier.spring_modulith_demo.supplier.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
  Supplier toDomain(SupplierEntity supplierEntity);
}
