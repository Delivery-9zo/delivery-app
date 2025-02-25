package com.sparta.deliveryapp.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "메뉴 추가 요청 Request")
public record MenuPostRequestDto(
    @JsonProperty("store_uuid") @Schema(description = "상점 ID",
        example = "58143fce-d13e-4073-b97d-73f8c9f97fb3")
    UUID storeId,

    @JsonProperty("menu_name") @Schema(description = "메뉴 이름", example = "치킨")
    String name,

    @JsonProperty("menu_info") @Schema(description = "메뉴 설명", example = "맛있어요")
    String info,

    @Schema(description = "메뉴 가격", example = "15000")
    Long price,

    @Schema(description = "메뉴 이미지 URL",
        example = "example.com/image")
    String image
) {

}
