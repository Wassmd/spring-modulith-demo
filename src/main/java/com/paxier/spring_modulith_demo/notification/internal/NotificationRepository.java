package com.paxier.spring_modulith_demo.notification.internal;

import com.paxier.spring_modulith_demo.notification.Notification;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

  private final JdbcTemplate jdbc;

  public void enqueue(int orderId) {
    jdbc.update("""
        INSERT INTO notification.delivery (order_id, kind, status)
        VALUES (?, 'ORDER_PLACED', 'PENDING')
        ON CONFLICT (order_id, kind) DO NOTHING
        """, orderId);
  }

  public Optional<Notification> findByOrderId(int orderId) {
    return jdbc.query("""
        SELECT order_id, status, created_at FROM notification.delivery
        WHERE order_id = ? AND kind = 'ORDER_PLACED'
        """, (rs, row) -> new Notification(rs.getInt("order_id"), rs.getString("status"),
        rs.getObject("created_at", OffsetDateTime.class)), orderId).stream().findFirst();
  }
}
