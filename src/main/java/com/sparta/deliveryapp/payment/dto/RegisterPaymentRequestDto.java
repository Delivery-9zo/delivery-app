package com.sparta.deliveryapp.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterPaymentRequestDto {

    @NotNull(message = "Order Id cannot be null")
    private UUID orderId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 1, message = "결제 금액은 1원 이상 입력되어야 합니다.")
    private int paymentAmount;

}
