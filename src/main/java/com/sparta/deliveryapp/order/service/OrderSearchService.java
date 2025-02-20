package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSearchService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 ID - 1건 조회(CUSTOMER)
    public Object findOrderByOrderId(UUID orderId) {

        orderRepository.findById(orderId).orElse(null);

        return null;
    }


    // 사용자 ID - 목록 조회(CUSTOMER)


    // 전체 결과 조회 (CUSTOMER 제외)




}
