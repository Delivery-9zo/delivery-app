package com.sparta.deliveryapp.payment.controller;

import com.sparta.deliveryapp.payment.dto.PaymentAllResponseDto;
import com.sparta.deliveryapp.payment.service.MasterPaymentService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/payments")
@PreAuthorize("hasAuthority('ROLE_MASTER')")

public class MasterPaymentController {

    private final MasterPaymentService masterPaymentService;

    // 전사용자 결제 조회 - MASTER
    @GetMapping()
    public ResponseEntity<?> getAllPayments(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Page<PaymentAllResponseDto> paymentResponse = masterPaymentService.getAllPayments(userDetails.getUser());
            return ResponseEntity.ok(paymentResponse);
        } catch(AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", "AccessDeniedException: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", "Exception: " + e.getMessage()));
        }
    }
}
