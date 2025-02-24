package com.sparta.deliveryapp.order.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItemGetRequestDto {
  private UUID menuId;
  private UUID orderId;
}
