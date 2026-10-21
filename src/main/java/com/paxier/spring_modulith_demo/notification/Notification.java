package com.paxier.spring_modulith_demo.notification;

import java.time.OffsetDateTime;

public record Notification(int orderId, String status, OffsetDateTime createdAt) {
}
