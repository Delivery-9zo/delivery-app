package com.sparta.deliveryapp.order.controller;


import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
import com.sparta.deliveryapp.order.dto.OrderItemResponseDto;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.service.OrderItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "주문 상세 API", description = "주문상세 컨트롤러에 대한 설명입니다.")
@RequestMapping("/api/orderItem")
public class OrderItemController {

  private final OrderItemService orderItemService;

  @PostMapping()
  public ResponseEntity<OrderItemResponseDto> postOrderItem(@RequestBody OrderItemRequestDto requestDto){
    OrderItem orderItem = orderItemService.postOrderItem(requestDto);

    return ResponseEntity.ok().body(new OrderItemResponseDto(orderItem));
  }



}
