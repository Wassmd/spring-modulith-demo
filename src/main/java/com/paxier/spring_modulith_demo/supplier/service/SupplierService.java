package com.paxier.spring_modulith_demo.supplier.service;

import com.paxier.spring_modulith_demo.supplier.dto.Supplier;
import com.paxier.spring_modulith_demo.supplier.mapper.SupplierMapper;
import com.paxier.spring_modulith_demo.supplier.repository.SupplierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierService {
  private final SupplierRepository supplierRepository;
  private final SupplierMapper supplierMapper;

  @Transactional(readOnly = true)
  public List<Supplier> getSuppliersForProduct(long productId) {
    return supplierRepository.findByProductEntity_ProductId(productId).stream()
        .map(supplierMapper::toDomain)
        .toList();
  }
}
