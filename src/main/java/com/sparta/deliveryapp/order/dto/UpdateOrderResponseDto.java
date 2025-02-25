package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateOrderResponseDto {

    private UUID orderId;
    private UUID userId;
    private OrderState orderState;
    private int totalPrice;
    private OrderType orderType;
}
