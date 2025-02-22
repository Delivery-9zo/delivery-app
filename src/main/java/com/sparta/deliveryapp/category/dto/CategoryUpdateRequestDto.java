package com.sparta.deliveryapp.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequestDto {

  @JsonProperty("category_id")
  private UUID categoryId;

  @JsonProperty("category_name")
  private String categoryName;

  @JsonProperty("new_category_name")
  private String newCategoryName;
}
