package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegisterOrderResponseDto {

    private UUID orderId;

    private OrderState orderState;

    private UUID itemId;

    private OrderType orderType;

    private LocalDateTime orderTime;

    private int totalPrice;

    private String userAddress;

    private String orderMemo;

    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public RegisterOrderResponseDto(UUID orderId) {
        this.orderId = orderId;
        this.orderState = orderState;
    }
}
