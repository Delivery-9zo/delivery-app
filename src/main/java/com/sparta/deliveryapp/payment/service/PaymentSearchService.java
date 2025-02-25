package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_PAYMENT_ID));

        // 2. userId 확인
        userRepository.findById(user.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXISTS_PAYMENT_USER_ID));

        log.info("user.getRole()={}", user.getRole());
        log.info("UserRole.CUSTOMER={}", UserRole.CUSTOMER);
        // 3. 권한 확인
        if(user.getRole() != UserRole.CUSTOMER) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_ONLY_CUSTOMER);
        }

        // 4. 본인 결제 내역만 확인(결제 userId 와 인증객체의 userId 일치 여부)
        log.info("payment.getUserI()={}", payment.getUserId());
        log.info("user.getUserId()={}", user.getUserId());
        if(!payment.getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_ONLY_USER_ID);
        }

        log.info(paymentId + "의 결제내역 조회 종료");

        return new PaymentResponseDto(payment.getPaymentId(), payment.getOrderId()
            , payment.getUserId(), payment.getPaymentStatus()
            , payment.getPaymentAmount(), payment.getPaymentTime());
    }

    // 사용자별 결제 조회
    @Transactional(readOnly = true)
    public Page<PaymentByUserIdResponseDto> getPaymentsByUserId(UUID userId, Pageable pageable, User user) {

        if(user.getRole() != UserRole.CUSTOMER) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_ONLY_CUSTOMER);
        }

        if(!user.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_ONLY_USER_ID);
        }

        Page<Payment> paymentList = paymentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

        if (paymentList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXISTS_PAYMENT_ID);
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
