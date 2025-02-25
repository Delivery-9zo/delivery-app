package com.sparta.deliveryapp.category.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

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
