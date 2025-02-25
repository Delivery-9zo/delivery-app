package com.sparta.deliveryapp.order.controller;


import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
import com.sparta.deliveryapp.order.dto.OrderItemResponseDto;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(name = "주문 상세 API", description = "주문상세 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/orderItem")
public class OrderItemController {

  private final OrderItemService orderItemService;

  @Operation(summary = "주문상세 추가 기능", description = "주문메뉴를 추가하는 api")
  @PostMapping()
  public ResponseEntity<OrderItemResponseDto> postOrderItem(@RequestBody OrderItemRequestDto requestDto){
    OrderItem orderItem = orderItemService.postOrderItem(requestDto);

    return ResponseEntity.ok().body(new OrderItemResponseDto(orderItem));
  }

  @Operation(summary = "주문 조회 기능", description = "주문 내역을 조회하는 api")
  @GetMapping("/{order_uuid}")
  public List<OrderItemResponseDto> getOrderItems(@PathVariable UUID order_uuid){

    return orderItemService.getOrderItems(order_uuid);
  }

  @Operation(summary = "주문상세 조회 기능", description = "주문의 상세내역을 조회하는 api")
  @GetMapping("/{item_uuid}")
  public OrderItemResponseDto getOrderItem(@PathVariable UUID item_uuid){
    return orderItemService.getOrderItem(item_uuid);
  }

  @Operation(summary = "주문상세 수정 기능", description = "주문의 상세내역을 수정하는 api")
  @PutMapping()
  public ResponseEntity<Map<String,String>> putOrderItem(@RequestBody OrderItemRequestDto requestDto){
    orderItemService.putOrderItem(requestDto);

    return ResponseEntity.ok(Collections.singletonMap("message", "성공적으로 수정되었습니다."));
  }

  @Operation(summary = "주문상세 삭제 기능", description = "주문의 상세내역을 삭제하는 api")
  @DeleteMapping("{item_uuid}")
  public ResponseEntity<Map<String,String>> deleteOrderItem(@PathVariable UUID item_uuid){
    orderItemService.deleteOrderItem(item_uuid);

    return ResponseEntity.ok(Collections.singletonMap("message","성공적으로 삭제되었습니다."));
  }



}
