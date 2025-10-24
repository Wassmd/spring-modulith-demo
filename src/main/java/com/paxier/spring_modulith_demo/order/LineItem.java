package com.paxier.spring_modulith_demo.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders_line_items")
record LineItem(@Id Integer id, int product, int quantity) {
}