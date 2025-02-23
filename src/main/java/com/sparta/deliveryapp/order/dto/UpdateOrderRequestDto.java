package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateOrderRequestDto {

    @NotNull(message = "Order Id cannot be null")
    private UUID orderId;

    @NotNull(message = "OrderState cannot be null")
    private OrderState orderState;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 1, message = "결제 금액은 1원 이상 입력되어야 합니다.")
    private int totalPrice;

    private OrderType orderType;
}
