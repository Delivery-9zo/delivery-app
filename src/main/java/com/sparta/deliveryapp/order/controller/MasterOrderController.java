package com.sparta.deliveryapp.order.controller;

import com.sparta.deliveryapp.order.dto.*;
import com.sparta.deliveryapp.order.service.OrderSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/master/orders")
@RequiredArgsConstructor
public class MasterOrderController {

    private final OrderSearchService orderSearchService;  // 주문 조회


    // 전체 주문 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
    public ResponseEntity<?> OrderList(@PageableDefault(size = 10) Pageable pageable) {

        List<SearchOrderResponseDto> searchOrderResponseDtoList = orderSearchService.findAllOrder(pageable);

        return ResponseEntity.ok(searchOrderResponseDtoList);
    }

}
