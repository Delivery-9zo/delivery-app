package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SearchOrderResponseDto {

    private UUID orderId;  // 주문 UUID

    private UUID userId;  // 유저 UUID

    private OrderType orderType;

    private LocalDateTime orderTime;

    private int totalPrice;

    private String userAddress;

    private String orderMemo;

    private OrderState orderState;

    private List<SearchOrderItemResponseDto> itemList;

    @Builder
    public SearchOrderResponseDto(UUID orderId, UUID userId, OrderType orderType, LocalDateTime orderTime, int totalPrice, String userAddress, String orderMemo, OrderState orderState
//            , List<SearchOrderItemResponseDto> itemList
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.orderTime = orderTime;
        this.totalPrice = totalPrice;
        this.userAddress = userAddress;
        this.orderMemo = orderMemo;
        this.orderState = orderState; // 추가
//        this.itemList = itemList; // 추가
    }

}
