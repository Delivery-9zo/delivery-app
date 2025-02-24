package com.sparta.deliveryapp.category.dto;


import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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
public class CategoryResponseDto {

  @NotBlank
  private UUID categoryId;

  @NotBlank
  private String categoryName;

  @NotBlank
  private LocalDateTime createAt;
}
