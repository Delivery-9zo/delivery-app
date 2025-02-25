package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        //Order order = orderRepository.findByOrderIdAndUserId(orderId, user.getUserId()).orElse(null);
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));

//        // 주문 상세 데이터 조회 후 Dto로 변환
//        List<SearchOrderItemResponseDto> itemList = order.getOrderItems().stream()
//                .map(orderItem -> {
//                    return orderItem.toSearchOrderItemResponseDto(orderItem);
//                }).toList();

//        return order.toSearchOrderByOrderIdResponseDto(order, itemList);
        return order.toSearchOrderByOrderIdResponseDto(order);
    }


    // 사용자 ID - 목록 조회(CUSTOMER)
    public Page<SearchOrderResponseDto> findOrdersByUserId(Pageable pageable, User user) {

        List<Order> orderList = orderRepository.findAllByUserId(user.getUserId());
        if (orderList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID);
        }

        return orderRepository.findByUserId(pageable, user.getUserId())
                .map(order -> new SearchOrderResponseDto(
                    order.getUserId(),
                    order.getOrderId(),
                    order.getOrderType(),
                    order.getOrderTime(),
                    order.getTotalPrice(),
                    order.getUserAddress(),
                    order.getOrderMemo(),
                    order.getOrderState()
//                        ,
//                    order.getOrderItems().stream()
//                            .map(orderItem -> {
//                                return orderItem.toSearchOrderItemResponseDto(orderItem);
//                            }).toList()
                ));
    }


    // 전체 결과 조회 (CUSTOMER 제외)
    public Page<SearchOrderResponseDto> findAllByOrderByCreatedAtDesc(Pageable pageable) {

        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(order -> new SearchOrderResponseDto(
                order.getUserId(),
                order.getOrderId(),
                order.getOrderType(),
                order.getOrderTime(),
                order.getTotalPrice(),
                order.getUserAddress(),
                order.getOrderMemo(),
                order.getOrderState()
//                    ,
//                order.getOrderItems().stream()
//                        .map(orderItem -> {
//                            return orderItem.toSearchOrderItemResponseDto(orderItem);
//                        }).toList()
            ));
    }

}
