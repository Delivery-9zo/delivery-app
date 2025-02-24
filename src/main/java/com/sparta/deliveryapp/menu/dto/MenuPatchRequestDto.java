package com.sparta.deliveryapp.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MenuPatchRequestDto(
    @JsonProperty("menu_name") String name,
    @JsonProperty("menu_info") String info,
    Long price,
    String image
) {

}
