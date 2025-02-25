package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.order.dto.UpdateOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.service.OrderRegisterService;
import com.sparta.deliveryapp.order.service.OrderSearchService;
import com.sparta.deliveryapp.order.service.OrderStatusService;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
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
@RequiredArgsConstructor
@Tag(name = "주문 API", description = "마스트 권한을 제외한 주문 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRegisterService orderRegisterService;
    private final OrderStatusService orderStatusService;
    private final OrderSearchService orderSearchService;

    // 주문 취소(SUCCESS -> CANCEL) : CUSTOMER / MANAGER,OWNER
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_MANGER', 'ROLE_OWNER')")
    @DeleteMapping("/{orderId}/{storeId}")
    @Operation(summary = "주문삭제 기능", description = "주문 id로 특정 상점의 주문을 삭제하는 api")
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

    // 주문 등록(WAIT) -> 주문상세 메뉴를 추가하면 주문등록이 됨
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_MANGER', 'ROLE_OWNER')")
    @PostMapping("/{storeId}/{menuId}/{quantity}")
    @Operation(summary = "주문등록 기능", description = "주문메뉴를 추가하는 장바구니 담기 기능 같은 주문을 등록하는 api")
    public ResponseEntity<?> postOrder(
            @PathVariable(name = "storeId") UUID storeId,
            @PathVariable(name = "menuId") UUID menuId,
            @PathVariable(name = "quantity") int quantity,
            @RequestBody RegisterOrderRequestDto registerOrderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 권한으로 고객 비대면, 매니저/오너 대면 서비스 분리
        try {
            log.info("주문 등록 컨트롤러 시작");
            UUID orderId = orderRegisterService.postOrder(storeId, menuId, quantity, registerOrderRequestDto, userDetails.getUser());
            log.info("주문 등록 컨트롤러 종료");

            return ResponseEntity.ok().body("주문 id = " + orderId);

        } catch (Exception e) {
            log.error("주문 상태 업데이트 중 오류 발생={}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message" + e.getMessage());
        }
    }

    // 주문 완료 - 상태수정(SUCCESS) -> 결제 등록(SUCCESS) : CUSTOMER, MANAGER, OWNER
    @PutMapping("/success/{orderId}")
    @Operation(summary = "주문수정 기능", description = "대면/비대면 분기처리하여 주문상태를 SUCCESS 로 변경하는 주문 완료하는 api")
    public ResponseEntity<?> updateOrder(@PathVariable(name = "orderId") UUID orderId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 권한으로 고객 비대면, 매니저/오너 대면 서비스 분리
        try {
            if (userDetails.getUser().getRole() == UserRole.CUSTOMER) {
                log.info("고객 비대면 주문완료 컨트롤러 시작");
                UpdateOrderResponseDto responseDto = orderStatusService.updateOrderNotFace(orderId, userDetails.getUser());
                log.info("고객 비대면 주문완료 컨트롤러 종료");
                return ResponseEntity.ok(responseDto);
            } else if (userDetails.getUser().getRole() == UserRole.MANAGER || userDetails.getUser().getRole() == UserRole.OWNER) {
                log.info("매니저, 오너의 대면 주문완료 컨트롤러 시작");
                UpdateOrderResponseDto responseDto = orderStatusService.updateOrderFace(orderId, userDetails.getUser());
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
    @Operation(summary = "주문조회 기능", description = "고객이 주문 id로 주문 조회하는 api")
    @ApiResponse(responseCode = "200", description = "성공"
            , content = @Content(
                    schema = @Schema(implementation = SearchOrderResponseDto.class)))
    public ResponseEntity<SearchOrderResponseDto> getOrderByOrderId(@PathVariable("orderId") UUID orderId,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        SearchOrderResponseDto searchOrderResponseDto = orderSearchService.findOrderByOrderId(orderId, userDetails.getUser());

        return ResponseEntity.ok(searchOrderResponseDto);
    }

    // 사용자 ID - 목록 조회(CUSTOMER)
    @GetMapping("/user")
    @Operation(summary = "사용자별 주문조회 기능", description = "고객의 주문 전체 조회하는 api")
    @ApiResponse(responseCode = "200", description = "성공"
            , content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = SearchOrderResponseDto.class))))
    public ResponseEntity<Page<SearchOrderResponseDto>> getOrdersByUserId(@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<SearchOrderResponseDto> searchOrderResponseDtoList = orderSearchService.findOrdersByUserId(pageable, userDetails.getUser());

        return ResponseEntity.ok(searchOrderResponseDtoList);
    }

}
