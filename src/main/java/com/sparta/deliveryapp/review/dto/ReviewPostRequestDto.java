package com.sparta.deliveryapp.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewPostRequestDto(

    @Schema(description = "리뷰 내용", example = "맛있어요")
    String comment,

    @Schema(description = "리뷰 사진 URL", example = "test.com/image/")
    String image,

    @Schema(description = "리뷰 점수", example = "3")
    Integer rating
) {

}
