package com.sparta.deliveryapp.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateRequestDto {

  @NotNull
  @JsonProperty("store_id")
  private UUID storeId;

  @NotBlank
  @JsonProperty("store_name")
  private String storeName;

  @NotBlank
  @JsonProperty("address")
  private String address;

  @NotBlank
  @JsonProperty("b_regi_num")
  private String bRegiNum;

  @NotNull
  @JsonProperty("open_at")
  private String openAt;

  @NotNull
  @JsonProperty("close_at")
  private String closeAt;

  @JsonProperty("category")
  private List<String> categories;
}
