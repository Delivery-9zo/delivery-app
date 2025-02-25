package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 등록 - 대면, 비대면
    public RegisterPaymentResponseDto postPayment(RegisterPaymentRequestDto requestDto, User user) {

        log.info("postPayment 결제 등록 시작");
        log.info("결제 요청 DTO: {}", requestDto);

        log.info("결제 금액: {}", requestDto.getPaymentAmount());
        if(requestDto.getPaymentAmount() <= 0) {
            throw new CustomException(ErrorCode.NOT_SUFFICIENT_PAYMENT_AMOUNT);
        }

        // 결제 등록 처리
        log.info("user.getUserId()={}", user.getUserId());

        // PG사 API 연결 -> 결제완료로 가정
        Payment paymentByOrder = Payment.builder()
                .orderId(requestDto.getOrderId())
                .paymentAmount(requestDto.getPaymentAmount())
                .userId(user.getUserId())
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentTime(LocalDateTime.now())
                .build();

        // 결제 저장
        Payment savedPayment = paymentRepository.save(paymentByOrder);

        log.info("postPayment 결제 등록 종료");
        
        return new RegisterPaymentResponseDto(savedPayment);
    }
}
