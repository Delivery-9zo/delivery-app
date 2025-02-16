package com.sparta.deliveryapp.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
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
public class StoreEntity {

  @Id
  @UuidGenerator
  private UUID storeId;

//  @ManyToOne(fetch = FetchType.LAZY) // N:1 관계 설정
//  @JoinColumn(name = "user_uuid", nullable = false)
//  private UserEntity user; // 가게를 소유한 유저 정보

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "address")
  private String address;

  @Column(name = "b_regi_num", nullable = false)
  private String bRegiNum;

  @Column(name = "rating")
  private String rating;

  @Column(name = "open_at")
  private Timestamp openAt;

  @Column(name = "close_at")
  private Timestamp closeAt;

  @Column(name = "store_x")
  private Double storeCoordX;

  @Column(name = "store_y")
  private Double storeCoordY;
}
