package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.service.OrderRegisterService;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRegisterService orderRegisterService;
    @Autowired
    private final OrderStatusService orderStatusService;

    // 주문 취소(SUCCESS -> CANCEL) : CUSTOMER / MANAGER,OWNER
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_MANGER', 'ROLE_OWNER')")
    @DeleteMapping("/{orderId}/{storeId}")
    public ResponseEntity<String> deleteOrder(@PathVariable(name = "orderId")  UUID orderId,
                                              @PathVariable(name = "storeId") UUID storeId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("deleteOrder 컨트롤러 시작");
        try {
            if(userDetails.getUser().getRole() == UserRole.MANAGER || userDetails.getUser().getRole() == UserRole.OWNER) {
                Order deletedOrder = orderStatusService.deleteOrder(orderId, storeId, userDetails.getUser());
                log.info("매니저,오너 - deleteOrder 컨트롤러 종료");

                return ResponseEntity.ok().body(deletedOrder.getDeletedAt() + " "
                        + deletedOrder.getUserId().toString() + "고객님의 주문 및 결제가 취소되었습니다.");
            } else {
                Order deletedOrder = orderStatusService.deleteOrderCustomer(orderId, storeId, userDetails.getUser());
                log.info("deleteOrder 컨트롤러 종료");

                return ResponseEntity.ok().body(deletedOrder.getDeletedAt()
                        + " 주문 및 결제가 취소되었습니다. 환불은 카드 영업일 1-3일 소요됩니다.");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body("message : " + e.getReason());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("message : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("message : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message" + e.getMessage());
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
