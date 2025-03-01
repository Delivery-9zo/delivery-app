package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
import com.sparta.deliveryapp.order.dto.OrderItemResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemService {
  private final OrderItemRepository orderItemRepository;
  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;


  public OrderItem postOrderItem(OrderItemRequestDto requestDto) {
    Menu menu = menuRepository.findById(requestDto.getMenuId()).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_MENU_ID));

    Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));

    OrderItem orderItem = new OrderItem(order,menu,requestDto.getQuantity());
    return orderItemRepository.save(orderItem);
  }

  public List<OrderItemResponseDto> getOrderItems(UUID order_uuid) {
    Order order = orderRepository.findById(order_uuid).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));

    return orderItemRepository.findByOrderId(order).stream()
        .map(OrderItemResponseDto::new).toList();
  }

  public OrderItemResponseDto getOrderItem(UUID itemUuid) {
    OrderItem orderItem = orderItemRepository.findById(itemUuid).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_ITEM_ID));

    return new OrderItemResponseDto(orderItem);
  }

  public void putOrderItem(OrderItemRequestDto requestDto) {

    Menu menu = menuRepository.findById(requestDto.getMenuId()).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_MENU_ID));

    Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));

    OrderItem orderItem = new OrderItem(order,menu,requestDto.getQuantity());


    // requestDto의 null을 제외한 필드만 복사
    NullAwareBeanUtils.copyNonNullProperties(requestDto, orderItem);
    orderItemRepository.save(orderItem);
  }

  public void deleteOrderItem(UUID itemUuid) {
    OrderItem orderItem = orderItemRepository.findById(itemUuid).orElseThrow(()->
        new CustomException(ErrorCode.NOT_EXISTS_ITEM_ID));

    // 소프트 삭제
    String deletedBy = getCurrentUserEmail();
    orderItemRepository.deleteOrderItem(deletedBy, orderItem.getItemId());
  }

  private String getCurrentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if(auth != null && auth.getPrincipal() instanceof UserDetails) {
      return ((UserDetails) auth.getPrincipal()).getUsername();
    }
    throw new SecurityException("No authenticated user found.");
  }
}
