package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderItem;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemResponseDto {
  private UUID orderId;
  private UUID menuId;
  private int quantity;

  public OrderItemResponseDto(OrderItem orderItem) {
    this.orderId = orderItem.getOrderId().getOrderId();
    this.menuId = orderItem.getMenuId().getId();
    this.quantity = orderItem.getQuantity();
  }
}
