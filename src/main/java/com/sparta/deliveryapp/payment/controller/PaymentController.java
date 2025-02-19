package com.sparta.deliveryapp.payment.controller;

import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.service.PaymentSearchService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentSearchService paymentSearchService;

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
}
