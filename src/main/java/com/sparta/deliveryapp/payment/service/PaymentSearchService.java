package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.PaymentByUserIdResponseDto;
import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSearchService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    // 결제 조회 - paymentId
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID paymentId, User user) {

        log.info(paymentId + "의 결제내역 조회 시작");

        // 1. paymentId 확인
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 결제 내역이 없습니다."));

        userRepository.findById(user.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("결제한 회원이 존재하지 않습니다."));

        if(user.getRole() == null || user.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("CUSTOMER 권한을 가진 사용자만 조회할 수 있습니다.");
        }

        log.info(paymentId + "의 결제내역 조회 종료");

        return new PaymentResponseDto(payment.getPaymentId(), payment.getOrderId()
                , payment.getUserId(), payment.getPaymentStatus()
                , payment.getPaymentAmount(), payment.getPaymentTime());
    }

    // 사용자별 결제 조회
    @Transactional
    public Page<PaymentByUserIdResponseDto> getPaymentByUserId(UUID userId, User user) {

        if(user.getRole() != UserRole.CUSTOMER) {
           throw new AccessDeniedException("CUSTOMER 권한만 결제 조회 가능합니다.");
        }

        if(!user.getUserId().equals(userId)) {
           throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> paymentList = paymentRepository.findAllByUserId(userId, pageable);

        if (paymentList.isEmpty()) {
            throw new NoSuchElementException("결제 내역이 없습니다.");
        }

        List<PaymentByUserIdResponseDto> paymentResponseList = paymentList.getContent().stream()
                .map(payment -> new PaymentByUserIdResponseDto(
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
