package com.sparta.deliveryapp.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 응답 값에 거리와 카테고리가 포함된 가게 정보 DTO
public class StoreNearbyStoreWithCategoryResponseDto {

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

  private List<String> categories;

  private Double distanceFromRequest;

  private Double rating;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
