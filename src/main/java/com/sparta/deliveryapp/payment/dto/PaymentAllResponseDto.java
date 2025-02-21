package com.sparta.deliveryapp.payment.dto;

import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAllResponseDto {

    private List<Payment> paymentList;

    public PaymentAllResponseDto(UUID paymentId, UUID userId, UUID orderId, PaymentStatus paymentStatus, int paymentAmount, LocalDateTime paymentTime) {
        this.paymentList = new ArrayList<>();
        this.paymentList.add(new Payment(paymentId, orderId, userId, paymentStatus, paymentAmount, paymentTime));
    }
}
