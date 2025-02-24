package com.sparta.deliveryapp.order.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrderItemRequestDto {

    @Parameter(description = "메뉴 uuid")
    @Schema(description = "메뉴 uuid를 입력하세요.")
    private UUID menuId;

    @Parameter(description = "메뉴 수량")
    @Schema(description = "메뉴 수량을 입력하세요.")
    private int quantity;

    @Parameter(description = "메뉴 가격")
    @Schema(description = "메뉴 가격을 입력하세요.")
    private int price;

}
