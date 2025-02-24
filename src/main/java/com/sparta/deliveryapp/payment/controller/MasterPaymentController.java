package com.sparta.deliveryapp.payment.controller;

import com.sparta.deliveryapp.payment.dto.PaymentAllResponseDto;
import com.sparta.deliveryapp.payment.service.MasterPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "마스터-결제 API", description = "마스터 권한을 가진 사람이 사용가능한 결제 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/master/payments")
@PreAuthorize("hasAuthority('ROLE_MASTER')")

public class MasterPaymentController {

    private final MasterPaymentService masterPaymentService;

    // 전사용자 결제 조회 - MASTER
    @GetMapping()
    @Operation(summary = "결제 전체조회 기능", description = "전체 결제 내역을 조회하는 api")
    @ApiResponse(responseCode = "200", description = "성공"
            , content = @Content(
            array = @ArraySchema(
                    schema = @Schema(implementation = PaymentAllResponseDto.class))))
    public ResponseEntity<?> getAllPayments(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        try {
            Page<PaymentAllResponseDto> paymentResponse = masterPaymentService.getAllPayments(pageable);
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
