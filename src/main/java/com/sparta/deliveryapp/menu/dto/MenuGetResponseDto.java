package com.sparta.deliveryapp.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record MenuGetResponseDto(

    @JsonProperty("menu_uuid")
    @Schema(description = "요청한 메뉴 Id", example = "6a56a22a-6d8a-4f86-86ae-4a2b712cdaac")
    UUID menuId,

    @Schema(description = "가게 Id", example = "58143fce-d13e-4073-b97d-73f8c9f97fb3")
    @JsonProperty("store_uuid") UUID storeId,

    @Schema(description = "메뉴 이름", example = "치킨")
    @JsonProperty("menu_name") String name,

    @Schema(description = "메뉴 설명", example = "맛있는 치킨")
    @JsonProperty("menu_info")
    String info,

    @Schema(description = "메뉴 가격", example = "15000")
    Long price,

    @Schema(description = "메뉴 이미지 URL", example = "example.com/image")
    String image
) {

}
