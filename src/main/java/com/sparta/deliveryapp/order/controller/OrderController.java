package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.OrderToCanceledRequestDto;
import com.sparta.deliveryapp.order.dto.OrderToCanceledResponseDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.service.OrderRegisterService;
import com.sparta.deliveryapp.order.service.OrderService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRegisterService orderRegisterService;

    // 주문 취소(SUCCESS -> CANCEL)
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> updateOrderStateToCancel(@PathVariable("orderId")  UUID orderId,
                                                      @RequestBody OrderToCanceledRequestDto orderToCanceledRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Authenticated User : {}", userDetails);
        try {
            OrderToCanceledResponseDto responseDto = orderService.updateOrderStateToCancel(orderId, orderToCanceledRequestDto, userDetails.getUser());
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

    // 주문 등록(WAIT)
    @PostMapping()
    public ResponseEntity<?> OrdersSave(@RequestBody RegisterOrderRequestDto registerOrderRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("Authenticated User : {} ", userDetails);

        orderRegisterService.addOrder(registerOrderRequestDto, userDetails.getUser());

        return ResponseEntity.ok(null);
    }

}
