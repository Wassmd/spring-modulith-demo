package com.paxier.spring_modulith_demo.order;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
record Order(@Id Integer orderId, Set<LineItem> lineItems) {
}