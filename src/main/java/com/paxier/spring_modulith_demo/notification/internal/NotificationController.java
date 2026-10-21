package com.paxier.spring_modulith_demo.notification.internal;

import com.paxier.spring_modulith_demo.notification.Notification;
import com.paxier.spring_modulith_demo.notification.NotificationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/notifications/orders")
@RequiredArgsConstructor
class NotificationController {

  private final NotificationService notifications;

  @GetMapping("/{orderId}")
  Notification findByOrderId(@PathVariable @Positive int orderId) {
    return notifications.findByOrderId(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not queued"));
  }
}
