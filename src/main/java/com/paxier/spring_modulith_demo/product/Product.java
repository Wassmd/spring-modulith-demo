package com.paxier.spring_modulith_demo.product;

import jakarta.validation.constraints.NotNull;

public record Product(
    long productId,
    @NotNull String name,
    @NotNull Double price) {
}
