package com.sparta.deliveryapp.store.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_store")
@Builder
public class StoreEntity extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID storeId;

  //유저 id 받아오기
  

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "address")
  private String address;

  @Column(name = "b_regi_num", nullable = false)
  private String bRegiNum;

  @Column(name = "rating")
  private String rating;

  @Column(name = "open_at")
  private LocalTime openAt;

  @Column(name = "close_at")
  private LocalTime closeAt;

  @Column(name = "store_x")
  private Double storeCoordX; // 경도

  @Column(name = "store_y")
  private Double storeCoordY; // 위도


}
