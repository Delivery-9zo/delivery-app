package com.sparta.deliveryapp.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SearchOrderResponseDto {

    private UUID orderId;

    @Builder
    public SearchOrderResponseDto(UUID orderId) {
        this.orderId = orderId;
    }
}
