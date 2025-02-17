package com.sparta.deliveryapp.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
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


  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

//  @Column(name = "deleted_by")
//  private String deletedBy;

  @PreRemove
  public void onPreRemove() {
    this.deletedAt = LocalDateTime.now();
    // this.deletedBy = getCurrentUser();
  }

  private boolean isDeleted() {
    return deletedAt != null;
  }

  // TODO: 추후 SecurityContext 가져와야함
//  private String getCurrentUser() {
//    // 예시로, Spring Security로 현재 사용자 가져오기
//    return SecurityContextHolder.getContext().getAuthentication().getName();
//  }

  // soft 삭제를 위한 set 추가
  public void setDeleteAt(LocalDateTime deletedAt){
    this.deletedAt = deletedAt;
  }
}
