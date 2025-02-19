package com.sparta.deliveryapp.store.dto;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.store.entity.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//응답 값에 거리가 포함되지 않은 가게 정보
public class StoreResponseDto {

  @NotBlank
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

}
