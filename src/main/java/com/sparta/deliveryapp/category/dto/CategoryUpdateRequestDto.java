package com.sparta.deliveryapp.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

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
