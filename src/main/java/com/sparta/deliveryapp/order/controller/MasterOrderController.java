package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.service.MasterOrderStatusService;
import com.sparta.deliveryapp.order.service.OrderSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.Parameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "마스터-주문 API", description = "마스터 권한을 가진 사람이 사용가능한 주문 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/master/orders")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
public class MasterOrderController {

    private final OrderSearchService orderSearchService;
    private final MasterOrderStatusService masterOrderStatusService;

    @DeleteMapping("/{orderId}/{storeId}")
    @Operation(summary = "가게의 주문삭제 기능", description = "주문 id로 특정 상점의 주문을 삭제하는 api")
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

    // 전체 주문 조회
    @GetMapping
    @Operation(summary = "주문 전체조회 기능",
            description = "전체 주문 내역을 조회하는 api",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 전체조회 성공"
                            , content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = SearchOrderResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "해당 권한을 가지지 않음"
                            , content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Parameters.FailReason.class))))
            }
    )
    public ResponseEntity<Page<SearchOrderResponseDto>> getOrdersByMaster(@PageableDefault(
            size = 10,
            page = 0,
            sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SearchOrderResponseDto> searchOrderResponseDtoList = orderSearchService.findAllByOrderByCreatedAtDesc(pageable);

        return ResponseEntity.ok(searchOrderResponseDtoList);
    }

}
