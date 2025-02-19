package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSearchService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID paymentId, User user) {

        log.info(paymentId + "의 결제내역 조회 시작");

        // 1. paymentId 확인
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 결제 내역이 없습니다."));

        User findUser = userRepository.findById(user.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("결제한 회원이 존재하지 않습니다."));

        if(user.getRole() == null || user.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("CUSTOMER 권한을 가진 사용자만 조회할 수 있습니다.");
        }

        log.info(paymentId + "의 결제내역 조회 종료");

        return new PaymentResponseDto(payment.getPaymentId(), payment.getOrderId()
                , payment.getUserId(), payment.getPaymentStatus()
                , payment.getPaymentAmount(), payment.getPaymentTime());
    }
}
