package com.sparta.deliveryapp.payment.controller;

import com.sparta.deliveryapp.payment.dto.PaymentByUserIdResponseDto;
import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.service.PaymentSearchService;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "결제 API", description = "마스트 권한을 제외한 결제 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSearchService paymentSearchService;

    // 결제 등록 - 대면, 비대면
    @PostMapping("/notface")
    @Operation(summary = "결제 등록 기능", description = "주문수정 서비스에서 권한으로 주문타입(대면/비대면)으로 분기처리하여 주문상태(SUCCESS) 변경 및 결제 등록하는 api")
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
    @Operation(summary = "결제조회 기능", description = "고객이 결제 id로 결제 조회하는 api")
    @ApiResponse(responseCode = "200", description = "성공"
            , content = @Content(
                    schema = @Schema(implementation = PaymentResponseDto.class)))
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
    @Operation(summary = "사용자별 결제조회 기능", description = "고객의 결제 전체 조회하는 api")
    @ApiResponse(responseCode = "200", description = "성공"
            , content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = PaymentByUserIdResponseDto.class))))
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable(name = "userId") UUID userId,
                        @PageableDefault(size = 10, page = 0) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Page<PaymentByUserIdResponseDto> paymentResponse = paymentSearchService.getPaymentsByUserId(userId, pageable, userDetails.getUser());
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
