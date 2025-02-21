package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.dto.SearchOrderItemResponseDto;
import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSearchService {

    private final OrderRepository orderRepository;

    // 주문 ID - 1건 조회(CUSTOMER)
    public SearchOrderResponseDto findOrderByOrderId(UUID orderId, User user) {

        Order order = orderRepository.findByOrderIdAndUserId(orderId, user.getUserId()).orElse(null);

        // 주문 객체가 null일 경우
        if (order == null) {
            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
        }

        List<SearchOrderItemResponseDto> itemList = order.getOrderItems().stream().map(orderItem -> {
            return orderItem.toSearchOrderItemResponseDto();
        }).toList();

        return order.toSearchOrderResponseDto(itemList);
    }

}
