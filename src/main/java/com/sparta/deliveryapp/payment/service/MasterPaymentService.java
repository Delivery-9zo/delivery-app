package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.payment.dto.PaymentAllResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterPaymentService {

    private final PaymentRepository paymentRepository;

    // 전사용자 결제 조회 - MASTER
    @Transactional(readOnly = true)
    public Page<PaymentAllResponseDto> getAllPayments(Pageable pageable) {

        Page<Payment> paymentList = paymentRepository.findAllByOrderByCreatedAtDesc(pageable);

        if(paymentList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXISTS_PAYMENT_ID);
        }

        List<PaymentAllResponseDto> paymentResponseList = paymentList.getContent().stream()
                .map(payment -> new PaymentAllResponseDto(
                        payment.getPaymentId(),
                        payment.getUserId(),
                        payment.getOrderId(),
                        payment.getPaymentStatus(),
                        payment.getPaymentAmount(),
                        payment.getPaymentTime()))
                .collect(Collectors.toList());

        return new PageImpl<>(paymentResponseList, pageable, paymentList.getTotalElements());
    }
}
