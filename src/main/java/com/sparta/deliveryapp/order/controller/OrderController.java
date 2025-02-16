package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.OrderResponseDto;
import com.sparta.deliveryapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문상태 변경(WAIT -> CANCEL)
    // user PR 후, 권한 확인
    @PutMapping("/state/{orderId}")
    public ResponseEntity<?> updateOrderStateToCancel(@PathVariable("orderId")  UUID orderId,
                                                                         @RequestBody OrderRequestDto orderRequestDto) {
        try {
            OrderResponseDto responseDto = orderService.updateOrderStateToCancel(orderId, orderRequestDto);
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
