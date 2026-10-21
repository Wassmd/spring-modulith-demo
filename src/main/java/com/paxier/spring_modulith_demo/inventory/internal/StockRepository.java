package com.paxier.spring_modulith_demo.inventory.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockRepository {

  private final JdbcTemplate jdbc;

  public int quantity(int productId) {
    return jdbc.query("SELECT quantity FROM inventory.stock WHERE product_id = ?",
        (rs, row) -> rs.getInt("quantity"), productId).stream().findFirst().orElse(0);
  }

  public int add(int productId, int quantity) {
    return jdbc.queryForObject("""
        INSERT INTO inventory.stock (product_id, quantity) VALUES (?, ?)
        ON CONFLICT (product_id) DO UPDATE SET quantity = inventory.stock.quantity + EXCLUDED.quantity
        RETURNING quantity
        """, Integer.class, productId, quantity);
  }

  // Atomic check-and-decrease: concurrent orders can never oversell.
  public boolean decrease(int productId, int quantity) {
    return jdbc.update("""
        UPDATE inventory.stock SET quantity = quantity - ?
        WHERE product_id = ? AND quantity >= ?
        """, quantity, productId, quantity) == 1;
  }
}
