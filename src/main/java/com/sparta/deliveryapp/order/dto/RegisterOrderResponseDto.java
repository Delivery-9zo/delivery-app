package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegisterOrderResponseDto {

    private UUID orderId;
    private OrderState orderState;

    @Builder
    public RegisterOrderResponseDto(UUID orderId, OrderState orderState) {
        this.orderId = orderId;
        this.orderState = orderState;
    }
}
