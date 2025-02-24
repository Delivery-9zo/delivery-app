package com.sparta.deliveryapp.order.controller;


import com.sparta.deliveryapp.order.dto.OrderItemGetRequestDto;
import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
import com.sparta.deliveryapp.order.dto.OrderItemResponseDto;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.service.OrderItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/{order_uuid}")
  public List<OrderItemResponseDto> getOrderItems(@PathVariable UUID order_uuid){

    return orderItemService.getOrderItems(order_uuid);
  }

  @GetMapping("/{item_uuid}")
  public OrderItemResponseDto getOrderItem(@PathVariable UUID item_uuid){
    return orderItemService.getOrderItem(item_uuid);
  }

  @PutMapping()
  public ResponseEntity<Map<String,String>> putOrderItem(@RequestBody OrderItemRequestDto requestDto){
    orderItemService.putOrderItem(requestDto);

    return ResponseEntity.ok(Collections.singletonMap("message", "성공적으로 수정되었습니다."));
  }

  @DeleteMapping("{item_uuid}")
  public ResponseEntity<Map<String,String>> deleteOrderItem(@PathVariable UUID item_uuid){
    orderItemService.deleteOrderItem(item_uuid);

    return ResponseEntity.ok(Collections.singletonMap("message","성공적으로 삭제되었습니다."));
  }



}
