package com.paxier.spring_modulith_demo.order;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
record Order(@Id Integer orderId, @NotEmpty(message = "line items should not be empty") Set<LineItem> lineItems) {
}