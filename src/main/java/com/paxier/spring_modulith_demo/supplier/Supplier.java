package com.paxier.spring_modulith_demo.supplier;

import jakarta.validation.constraints.NotNull;

public record Supplier(
    long supplierId,
    @NotNull String supplierName,
    long productId) {
}
