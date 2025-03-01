package com.sparta.deliveryapp.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {

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
