package com.sparta.deliveryapp.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record MenuPatchRequestDto(
    @JsonProperty("menu_name")
    @Schema(description = "수정할 메뉴 이름")
    String name,

    @JsonProperty("menu_info")
    @Schema(description = "메뉴 설명", example = "맛있는 치킨")
    String info,

    @Schema(description = "메뉴 가격", example = "15000")
    Long price,

    @Schema(description = "메뉴 이미지 URL", example = "example.com/image")
    String image
) {

}
