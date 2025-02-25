package com.sparta.deliveryapp.review.dto;

public record ReviewPostRequestDto(
    String comment,
    String image,
    Integer rating
) {

}
