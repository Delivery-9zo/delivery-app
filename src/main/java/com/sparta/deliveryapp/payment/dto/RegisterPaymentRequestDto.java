package com.sparta.deliveryapp.payment.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPaymentRequestDto {

    @NotNull(message = "Order Id cannot be null")
    private UUID orderId;

    @NotNull(message = "OrderState cannot be null")
    private OrderState orderState;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 1, message = "결제 금액은 1원 이상 입력되어야 합니다.")
    private int paymentAmount;

    private OrderType orderType;
}
