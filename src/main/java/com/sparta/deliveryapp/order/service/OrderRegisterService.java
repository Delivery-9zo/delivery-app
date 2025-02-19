package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderRegisterService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    public RegisterOrderResponseDto addOrder(RegisterOrderRequestDto registerOrderRequestDto, User user) {
        log.info("주문 등록 : registerOrderRequestDto={}", registerOrderRequestDto);

        // 주문 수량이 0일 경우
        if (registerOrderRequestDto.getQuantity() == 0) {
            throw new CustomException(ErrorCode.ITEM_SOLD_OUT);
        }

        // 메뉴의 수량이 0일 경우(메뉴가 품절일 경우)
        //TODO 메뉴 수량(메뉴 API 연동 예정, 현재는 에러나지 않도록 1로 기본값 설정)
        int menuQuantity = 1;
        if (menuQuantity == 0) {
            throw new CustomException(ErrorCode.NON_ZERO_PARAMETER);
        }


        // 총 금액 계산
        int totalPrice = calculateTotalPrice(registerOrderRequestDto.getPrice(), registerOrderRequestDto.getQuantity());

        OrderItem orderItem = OrderItem.builder()
                .userId(user.getUserId())
                .menuId(registerOrderRequestDto.getMenuId())
                .quantity(registerOrderRequestDto.getQuantity())
                .build();

        OrderItem saveOrderItem = orderItemRepository.save(orderItem);

        Order order = registerOrderRequestDto.toEntity(user.getUserId(), saveOrderItem.getItemId(), totalPrice);

        Order saveOrder = orderRepository.save(order);

        return RegisterOrderResponseDto.builder()
                .orderId(saveOrder.getOrderId())
                .build();
    }

    //총 금액 계산
    public int calculateTotalPrice(int price, int quantity) {
        return price * quantity;
    }

}
