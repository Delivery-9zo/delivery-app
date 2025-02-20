package com.sparta.deliveryapp.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record MenuPostRequestDto(
    @JsonProperty("store_uuid") UUID storeId,
    @JsonProperty("menu_name") String name,
    @JsonProperty("menu_info") String info,
    Long price,
    String image
) {

}
