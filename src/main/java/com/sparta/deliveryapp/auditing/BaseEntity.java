package com.sparta.deliveryapp.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

//  @CreatedBy
//  @Column(name = "created_by", updatable = false)
//  private String createdBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", updatable = true)
  private LocalDateTime updatedAt;

//  @LastModifiedBy
//  @Column(name = "updated_by", updatable = true)
//  private String updatedBy;

  private LocalDateTime deletedAt;

  private boolean isDeleted() {
    return deletedAt != null;
  }
}
