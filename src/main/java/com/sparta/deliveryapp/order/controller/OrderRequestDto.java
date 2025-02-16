package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private UUID orderId;
    private UUID menuId;
    private OrderType orderType;
    private LocalDateTime orderTime;
    private String email;
    private int totalPrice;
    private String userAddress;
    private String orderMemo;
    private OrderState orderState;
}
