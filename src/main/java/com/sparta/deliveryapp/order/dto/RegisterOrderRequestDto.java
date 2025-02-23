package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrderRequestDto {

//    private UUID menuId;
    private int quantity;
    private OrderType orderType;
    private int price;

    private String userAddress;
    private String orderMemo;
    private OrderState orderState;
    private List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList;

    public Order toEntity(UUID userId, int totalPrice) {
        return Order.builder()
            .userId(userId)
            .orderType(orderType)
            .totalPrice(totalPrice)
            .userAddress(userAddress)
            .orderMemo(orderMemo)
            .build();
    }

}
