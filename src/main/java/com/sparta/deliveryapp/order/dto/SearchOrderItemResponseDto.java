package com.sparta.deliveryapp.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class SearchOrderItemResponseDto {

    private UUID itemId;  // 주문 상세 ID

    private UUID menuId;  // 메뉴 ID

    private int quantity; // 수량

}
