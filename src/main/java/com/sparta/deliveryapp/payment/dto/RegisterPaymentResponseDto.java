package com.sparta.deliveryapp.payment.dto;

import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPaymentResponseDto {

    private UUID paymentId;
    private UUID orderId;
    private UUID userId;
    private PaymentStatus paymentStatus;
    private int paymentAmount;
    private LocalDateTime paymentTime;

}
