package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {
  private final OrderItemRepository orderItemRepository;
  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;


  public OrderItem postOrderItem(OrderItemRequestDto requestDto) {
    Menu menu = menuRepository.findById(requestDto.getMenuId()).orElseThrow(()->
        new IllegalArgumentException("메뉴가 존재하지 않습니다."));

    Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(()->
        new IllegalArgumentException("주문이 존재하지 않습니다."));

    OrderItem orderItem = new OrderItem(order,menu,requestDto.getQuantity());
    return orderItemRepository.save(orderItem);
  }
}
