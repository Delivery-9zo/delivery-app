package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrderItemRequestDto {

    private UUID menuId;
    private int quantity;


    public OrderItem toEntity(UUID userId) {
        return OrderItem.builder()
            .userId(userId)
            .menuId(menuId)
            .quantity(quantity)
            .build();
    }

}
