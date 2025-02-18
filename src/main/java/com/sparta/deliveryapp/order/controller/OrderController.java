package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.CancellationOrderRequestDto;
import com.sparta.deliveryapp.order.dto.CancellationOrderResponseDto;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderStatusService orderStatusService;

    // 주문 취소(SUCCESS -> CANCEL)
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> updateOrderStateToCancel(@PathVariable("orderId")  UUID orderId,
                                                      @RequestBody CancellationOrderRequestDto cancellationOrderRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("Authenticated User : {}", userDetails);
        try {
            CancellationOrderResponseDto responseDto = orderStatusService.updateOrderStateToCancel(orderId, cancellationOrderRequestDto, userDetails.getUser());
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            // ResponseStatusException 의 경우, 상태 코드와 메시지를 반환
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("message", e.getReason()));
        } catch (Exception e) {
            // 예외가 발생한 경우, 메시지를 반환
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
