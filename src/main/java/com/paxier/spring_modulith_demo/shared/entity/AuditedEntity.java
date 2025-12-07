package com.paxier.spring_modulith_demo.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditedEntity {
  @CreatedDate
  @Column(updatable = false)
  LocalDateTime createdAt;

  @CreatedBy
  @Column(updatable = false)
  String createdBy;

  @LastModifiedDate
  LocalDateTime modifiedAt;

  @LastModifiedBy
  String modifiedBy;

  @PrePersist
  void onCreate() {
    this.createdBy = getCurrentUser();
  }

  private String getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return "system";
    }

    // For OAuth2/JWT (Keycloak)
    if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
      String clientId = jwt.getClaimAsString("client_id");
      return clientId != null ? clientId : "system";
    }

    // Fallback for other authentication types
    String name = authentication.getName();
    return (name != null && !name.equals("anonymousUser")) ? name : "system";
  }
}

