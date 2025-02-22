package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.service.MasterOrderStatusService;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/orders")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
public class MasterOrderController {

    private final OrderStatusService orderStatusService;
    private final MasterOrderStatusService masterOrderStatusService;

    @DeleteMapping("/{orderId}/{storeId}")
    public ResponseEntity<String> deleteOrderMaster (@PathVariable(name = "orderId")  UUID orderId,
                                              @PathVariable(name = "storeId") UUID storeId) {
        log.info("deleteOrderMaster 컨트롤러 시작");
        try {
            Order deletedOrder = masterOrderStatusService.deleteOrderMaster(orderId, storeId);
            log.info("deleteOrderMaster 컨트롤러 종료");

            return ResponseEntity.ok().body(deletedOrder.getDeletedAt()
                    + " 관리자가 주문 및 결제를 취소했습니다.");
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

}
