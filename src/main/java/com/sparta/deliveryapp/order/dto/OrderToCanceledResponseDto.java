package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderToCanceledResponseDto {

    private UUID orderId;
    private UUID userId;
    private UUID itemId;
    private OrderType orderType;
    private LocalDateTime orderTime;
    private int totalPrice;
    private String userAddress;
    private String orderMemo;
    private OrderState orderState;

    public OrderToCanceledResponseDto(Order updateOrder) {
        this.orderId = updateOrder.getOrderId();
        this.itemId = updateOrder.getItemId();
        this.orderType = updateOrder.getOrderType();
        this.orderTime = updateOrder.getOrderTime();
        this.userId = updateOrder.getUserId();
        this.totalPrice = updateOrder.getTotalPrice();
        this.userAddress = updateOrder.getUserAddress();
        this.orderMemo = updateOrder.getOrderMemo();
        this.orderState = updateOrder.getOrderState();
    }
}
