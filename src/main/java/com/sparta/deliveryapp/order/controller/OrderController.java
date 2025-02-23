package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.*;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.service.OrderRegisterService;
import com.sparta.deliveryapp.order.service.OrderSearchService;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final OrderStatusService orderStatusService;
    private final OrderSearchService orderSearchService;

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

    // 주문 등록(WAIT) -> 주문상세 수정(추가,수정,삭제)
    @PostMapping()
    public ResponseEntity<?> postOrder(@RequestBody RegisterOrderRequestDto registerOrderRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("Authenticated User : {} ", userDetails);

        RegisterOrderResponseDto responseDto = orderRegisterService.postOrder(registerOrderRequestDto, userDetails.getUser());

        return ResponseEntity.ok(responseDto);
    }

    // 주문완료 - 상태수정(SUCCESS) -> 결제 등록(SUCCESS) : CUSTOMER, MANAGER, OWNER
    @PutMapping("/success/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable(name = "orderId") UUID orderId,
                                                             @Valid @RequestBody UpdateOrderRequestDto updateOrderRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 권한으로 고객 비대면, 매니저/오너 대면 서비스 분리
        try {

            if (userDetails.getUser().getRole() == UserRole.CUSTOMER) {
                log.info("고객 비대면 주문완료 컨트롤러 시작");
                UpdateOrderResponseDto responseDto = orderStatusService.updateOrderNotFace(orderId, updateOrderRequestDto, userDetails.getUser());
                log.info("고객 비대면 주문완료 컨트롤러 종료");
                return ResponseEntity.ok(responseDto);
            } else if (userDetails.getUser().getRole() == UserRole.MANAGER || userDetails.getUser().getRole() == UserRole.OWNER) {
                log.info("매니저, 오너의 대면 주문완료 컨트롤러 시작");
                UpdateOrderResponseDto responseDto = orderStatusService.updateOrderFace(orderId, updateOrderRequestDto, userDetails.getUser());
                log.info("매니저, 오너의 대면 주문완료 컨트롤러  종료");
                return ResponseEntity.ok(responseDto);
            } else {
                log.warn("잘못된 권한으로 주문 업데이트 시도: {}", userDetails.getUser().getRole());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 권한입니다.");
            }
        } catch (Exception e) {
            log.error("주문 상태 업데이트 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message" + e.getMessage());
        }
    }

    // 주문 ID - 1건 조회(CUSTOMER)
    @GetMapping("/{orderId}")
    public ResponseEntity<SearchOrderResponseDto> getOrderByOrderId(@PathVariable("orderId") UUID orderId,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        SearchOrderResponseDto searchOrderResponseDto = orderSearchService.findOrderByOrderId(orderId, userDetails.getUser());

        return ResponseEntity.ok(searchOrderResponseDto);
    }

    // 사용자 ID - 목록 조회(CUSTOMER)
    @GetMapping("/user")
    public ResponseEntity<Page<SearchOrderResponseDto>> getOrdersByUserId(@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<SearchOrderResponseDto> searchOrderResponseDtoList = orderSearchService.findOrdersByUserId(pageable, userDetails.getUser());

        return ResponseEntity.ok(searchOrderResponseDtoList);
    }

}
