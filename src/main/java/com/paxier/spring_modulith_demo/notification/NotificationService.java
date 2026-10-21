package com.paxier.spring_modulith_demo.notification;

import com.paxier.spring_modulith_demo.notification.internal.NotificationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notifications;

  @Transactional(readOnly = true)
  public Optional<Notification> findByOrderId(int orderId) {
    return notifications.findByOrderId(orderId);
  }
}
