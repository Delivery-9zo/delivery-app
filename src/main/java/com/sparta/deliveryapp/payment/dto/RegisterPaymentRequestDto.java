package com.sparta.deliveryapp.payment.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Parameter(description = "주문 uuid")
    @Schema(description = "사용자의 주문 uuid를 입력하세요.")
    @NotNull(message = "Order Id cannot be null")
    private UUID orderId;

    @Parameter(description = "결제 금액")
    @Schema(description = "결제 금액이 필요합니다.")
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 1, message = "결제 금액은 1원 이상 입력되어야 합니다.")
    private int paymentAmount;

}
