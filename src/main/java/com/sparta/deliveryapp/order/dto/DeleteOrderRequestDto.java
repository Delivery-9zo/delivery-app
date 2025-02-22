package com.sparta.deliveryapp.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DeleteOrderRequestDto {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("item_id")
    private UUID itemId;

    @JsonProperty("order_type")
    private OrderType orderType;

    @JsonProperty("order_time")
    private LocalDateTime orderTime;

    @JsonProperty("total_price")
    private Integer totalPrice;

    @JsonProperty("user_address")
    private String userAddress;

    @JsonProperty("order_meno")
    private String orderMemo;

    @JsonProperty("order_state")
    private OrderState orderState;
}
