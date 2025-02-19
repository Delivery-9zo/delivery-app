package com.sparta.deliveryapp.order.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegisterOrderResponseDto {

    private UUID orderId;

    @Builder
    public RegisterOrderResponseDto(UUID orderId) {
        this.orderId = orderId;
    }
}
