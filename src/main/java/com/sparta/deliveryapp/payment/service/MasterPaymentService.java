package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.PaymentAllResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterPaymentService {

    private final PaymentRepository paymentRepository;

    // 전사용자 결제 조회 - MASTER
    @Transactional(readOnly = true)
    public Page<PaymentAllResponseDto> getAllPayments(User user) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> paymentList = paymentRepository.findAll(pageable);

        if(paymentList.isEmpty()) {
            throw new NoSuchElementException("결제 내역이 없습니다.");
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
        log.info("결제id={}", paymentResponseList.get(0).getPaymentList().get(0).getPaymentId());
        return new PageImpl<>(paymentResponseList, pageable, paymentList.getTotalElements());
    }
}
