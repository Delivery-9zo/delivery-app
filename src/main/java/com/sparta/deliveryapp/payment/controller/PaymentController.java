package com.sparta.deliveryapp.payment.controller;

import com.sparta.deliveryapp.payment.dto.PaymentByUserIdResponseDto;
import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.service.PaymentSearchService;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSearchService paymentSearchService;

    // 결제 등록 - 비대면
    @PostMapping("/notface")
    public ResponseEntity<?> postPayment(@Valid @RequestBody RegisterPaymentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            RegisterPaymentResponseDto registerPayment = paymentService.postPayment(requestDto, userDetails.getUser());
            return ResponseEntity.ok(registerPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 결제 조회 - paymentId
    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable(name = "paymentId") UUID paymentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("getPaymentById 컨트롤러 시작 : " + paymentId);

        try {
            PaymentResponseDto responseDto = paymentSearchService.getPaymentById(paymentId, userDetails.getUser());

            Map<String, Object> res = new HashMap<>();
            res.put("message", paymentId + "의 결제내역 조회에 성공했습니다.");
            res.put("payment", responseDto);

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", "IllegalArgumentException: " + e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", "AccessDeniedException: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "Exception:  " + e.getMessage()));
        }
    }

    // 사용자별 결제 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentByUserId(@PathVariable(name = "userId") UUID userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Page<PaymentByUserIdResponseDto> paymentResponse = paymentSearchService.getPaymentByUserId(userId, userDetails.getUser());
            return ResponseEntity.ok(paymentResponse);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "AccessDeniedException: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "IllegalArgumentException: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Exception: " + e.getMessage()));
        }
    }


}
