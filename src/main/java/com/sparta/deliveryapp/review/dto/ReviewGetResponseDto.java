package com.sparta.deliveryapp.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record ReviewGetResponseDto(
    @JsonProperty("review_uuid") UUID reviewId,
    String comment,
    @JsonProperty("store_uuid") UUID storeId,
    String image,
    Integer rating
) {

}

