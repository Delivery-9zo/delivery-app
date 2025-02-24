package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrderRequestDto {

//    private UUID menuId;

//    @Parameter(description = "메뉴 수량")
//    @Schema(description = "메뉴 수량을 입력하세요.")
//    private int quantity;
//
//    @Parameter(description = "메뉴 가격")
//    @Schema(description = "메뉴 가격이 필요합니다.")
//    private int price;

    @Parameter(description = "주문 타입")
    @Schema(description = "주문 타입(NON_FACE_TO_FACE, FACE_TO_FACE)을 입력하세요.")
    private OrderType orderType;

//    @Parameter(description = "주문 상태")
//    @Schema(description = "주문 상태가 필요합니다.")
//    private OrderState orderState;

    @Parameter(description = "사용자 주소")
    @Schema(description = "사용자의 배달 주소를 입력하세요.")
    private String userAddress;

    @Parameter(description = "주문 메모")
    @Schema(description = "주문 메모를 작성하세요.")
    private String orderMemo;

//    @Parameter(description = "주문 총금액")
//    @Schema(description = "주문 총금액을 작성하세요.")
//    private int totalPrice;

    private List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList;

    public Order toEntity(UUID userId, int totalPrice) {
        return Order.builder()
            .userId(userId)
            .orderType(orderType)
            .totalPrice(totalPrice)
            .userAddress(userAddress)
            .orderMemo(orderMemo)
            .build();
    }

}
