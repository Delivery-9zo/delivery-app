package com.sparta.deliveryapp.order.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemRequestDto {
  private UUID orderId;
  private UUID menuId;
  private int quantity;
}
