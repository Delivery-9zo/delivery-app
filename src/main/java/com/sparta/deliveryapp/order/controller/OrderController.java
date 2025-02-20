package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.CancellationOrderRequestDto;
import com.sparta.deliveryapp.order.dto.CancellationOrderResponseDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderResponseDto;
import com.sparta.deliveryapp.order.service.OrderRegisterService;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    private final OrderRegisterService orderRegisterService;
    private final OrderStatusService orderStatusService;

    // 주문 취소(SUCCESS -> CANCEL)
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> updateOrderStateToCancel(@PathVariable("orderId")  UUID orderId,
                                                      @RequestBody CancellationOrderRequestDto cancellationOrderRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Authenticated User : {}", userDetails);
        try {
            CancellationOrderResponseDto responseDto = orderStatusService.updateOrderStateToCancel(orderId, cancellationOrderRequestDto, userDetails.getUser());
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("message", e.getReason()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // 주문 등록(WAIT)
    @PostMapping()
    public ResponseEntity<?> OrdersSave(@RequestBody RegisterOrderRequestDto registerOrderRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("Authenticated User : {} ", userDetails);

        RegisterOrderResponseDto responseDto = orderRegisterService.addOrder(registerOrderRequestDto, userDetails.getUser());

        return ResponseEntity.ok(responseDto);
    }
}
