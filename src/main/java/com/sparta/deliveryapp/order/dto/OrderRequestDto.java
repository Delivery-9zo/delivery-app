package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private UUID orderId;
    private UUID itemId;
    private OrderType orderType;
    private LocalDateTime orderTime;
    private String email;
    private int totalPrice;
    private String userAddress;
    private String orderMemo;
    private OrderState orderState;
}
