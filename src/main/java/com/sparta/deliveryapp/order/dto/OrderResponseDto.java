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
public class OrderResponseDto {

    private UUID orderId;
    private UUID menuId;
    private OrderType orderType;
    private LocalDateTime orderTime;
    private String email;
    private int totalPrice;
    private String userAddress;
    private String orderMemo;
    private OrderState orderState;

    public OrderResponseDto(Order updateOrder) {
        this.orderId = updateOrder.getOrderId();
        this.menuId = updateOrder.getMenuId();
        this.orderType = updateOrder.getOrderType();
        this.orderTime = updateOrder.getOrderTime();
        this.email = updateOrder.getEmail();
        this.totalPrice = updateOrder.getTotalPrice();
        this.userAddress = updateOrder.getUserAddress();
        this.orderMemo = updateOrder.getOrderMemo();
        this.orderState = updateOrder.getOrderState();
    }
}
