package com.sparta.deliveryapp.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

<<<<<<< Updated upstream
  private List<String> categories;

=======
>>>>>>> Stashed changes
}
