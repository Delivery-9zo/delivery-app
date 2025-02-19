package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrderRequestDto {

    private UUID menuId;
    private OrderType orderType;
    private int price;
    private int quantity;
    private String userAddress;
    private String orderMemo;


    public Order toEntity(UUID userId, UUID itemId, int totalPrice) {
        return Order.builder()
                .userId(userId)
                .itemId(itemId)
                .orderType(orderType)
                .totalPrice(totalPrice)
                .userAddress(userAddress)
                .orderMemo(orderMemo)
                .build();
    }

}
