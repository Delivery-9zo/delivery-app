package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 등록
    public RegisterPaymentResponseDto postPayment(RegisterPaymentRequestDto requestDto, User user) {

        log.info("postPayment 결제 등록 시작");
        log.info("결제 요청 DTO: {}", requestDto);

//        // 1. 고객 권한 확인
//        if(user.getRole() != UserRole.CUSTOMER) {
//            throw new AccessDeniedException("결제는 CUSTOMER 사용자만 가능합니다.");
//        }
//
//        // 2. 주문 내역
//        log.info("requestDto.getOrderId()={}", requestDto.getOrderId());
//        if(requestDto.getOrderId() == null) {
//            throw new IllegalArgumentException("결제 가능한 주문내역이 존재하지 않습니다.");
//        }
//
//        // 3. 주문타입(NON_FACE_TO_FACE), 총주문금액, 주문상태(WAIT) 조건별 처리
//        log.info("주문 타입: {}", requestDto.getOrderType());
//        if(requestDto.getOrderType() != OrderType.NON_FACE_TO_FACE) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대면 주문과 결제는 가게에 문의해주세요!");
//        }
//
//        log.info("주문 상태: {}", requestDto.getOrderState());
//        if(requestDto.getOrderState() != OrderState.WAIT) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제가 완료되었습니다.");
//        }

        log.info("결제 금액: {}", requestDto.getPaymentAmount());
        if(requestDto.getPaymentAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 금액은 1원 이상이어야 합니다.");
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
