package com.paxier.spring_modulith_demo.product.dto;

import jakarta.validation.constraints.NotNull;

public record Product(
    Long productId,
    @NotNull String name,
    @NotNull Double price) {
}
