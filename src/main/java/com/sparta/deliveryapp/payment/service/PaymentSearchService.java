package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.PaymentAllResponseDto;
import com.sparta.deliveryapp.payment.dto.PaymentByUserIdResponseDto;
import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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

        // 2. userId 확인
        userRepository.findById(user.getUserId())
            .orElseThrow(()-> new IllegalArgumentException("결제한 회원이 존재하지 않습니다."));

        log.info("user.getRole()={}", user.getRole());
        log.info("UserRole.CUSTOMER={}", UserRole.CUSTOMER);
        // 3. 권한 확인
        if(user.getRole() != UserRole.CUSTOMER) {
            throw new AccessDeniedException("CUSTOMER 권한을 가진 사용자만 조회할 수 있습니다.");
        }

        // 4. 본인 결제 내역만 확인(결제 userId 와 인증객체의 userId 일치 여부)
        log.info("payment.getUserI()={}", payment.getUserId());
        log.info("user.getUserId()={}", user.getUserId());
        if(!payment.getUserId().equals(user.getUserId())) {
            throw new NoSuchElementException("본인의 결제 정보만 조회 가능합니다.");
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
            throw new AccessDeniedException("CUSTOMER 권한만 결제 조회 가능합니다.");
        }

        if(!user.getUserId().equals(userId)) {
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }

        Page<Payment> paymentList = paymentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

        if (paymentList.isEmpty()) {
            throw new NoSuchElementException("고객님의 결제 내역이 없습니다.");
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
