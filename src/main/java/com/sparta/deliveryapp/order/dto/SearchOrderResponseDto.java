package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderType;
import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SearchOrderResponseDto {

    private UUID orderId;  // 주문 UUID

    private UUID userId;  // 유저 UUID

    private UUID itemId;  // 주문 상세 UUID

    private OrderType orderType;

    private LocalDateTime orderTime;

    private int totalPrice;

    private String userAddress;

    private String orderMemo;

    private List<SearchOrderItemResponseDto> itemList = new ArrayList<>();

    private PaymentResponseDto paymentResponseDto;

}
