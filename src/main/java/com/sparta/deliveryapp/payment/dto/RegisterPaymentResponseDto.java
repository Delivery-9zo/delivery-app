package com.sparta.deliveryapp.payment.dto;

import com.sparta.deliveryapp.payment.entity.Payment;
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

    public RegisterPaymentResponseDto(Payment savedPayment) {
        this.paymentId = savedPayment.getPaymentId();
        this.orderId = savedPayment.getOrderId();
        this.userId = savedPayment.getUserId();
        this.paymentStatus = savedPayment.getPaymentStatus();
        this.paymentAmount = savedPayment.getPaymentAmount();
        this.paymentTime = savedPayment.getPaymentTime();
    }
}
