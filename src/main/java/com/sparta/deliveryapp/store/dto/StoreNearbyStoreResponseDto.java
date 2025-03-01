package com.sparta.deliveryapp.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 응답 값에 거리가 포함된 가게 정보 DTO
public class StoreNearbyStoreResponseDto {

  @NotNull
  private UUID storeId;

  @NotBlank
  private String storeName;

  @NotBlank
  private String address;

  @NotBlank
  private String bRegiNum;

  @NotNull
  private LocalTime openAt;

  @NotNull
  private LocalTime closeAt;

  private double distanceFromRequest;

  private Double rating;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
