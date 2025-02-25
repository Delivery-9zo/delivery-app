package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterOrderRequestDto {

    @Parameter(description = "주문 타입")
    @Schema(description = "주문 타입(NON_FACE_TO_FACE, FACE_TO_FACE)을 입력하세요.")
    private OrderType orderType;

    @Parameter(description = "사용자 주소")
    @Schema(description = "사용자의 배달 주소를 입력하세요.")
    private String userAddress;

    @Parameter(description = "주문 메모")
    @Schema(description = "주문 메모를 작성하세요.")
    private String orderMemo;

//    private List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList;

    public Order toEntity() {
        return Order.builder()
            .orderType(orderType)
            .userAddress(userAddress)
            .orderMemo(orderMemo)
            .build();
    }

}
