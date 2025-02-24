package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.order.dto.RegisterOrderItemRequestDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderItemRegisterService {

    private final OrderItemRepository orderItemRepository;


    /**
     * 주문 상세 등록
     * @param registerOrderItemRequestDtoList
     * @param order
     * @param user
     */
    @Transactional
    public void postOrderItem(List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList, Order order, User user) {
        log.info("주문 상세 등록 : postOrderItem={}", registerOrderItemRequestDtoList);
        log.info("주문 상세 등록 : order={}", order);
        log.info("주문 상세 등록 : user={}", user);


        registerOrderItemRequestDtoList.forEach(roi -> {
            // 주문 상세 객체 저장
            UUID userId = null;
            if (user != null) {
                userId = user.getUserId();
            }

            OrderItem orderItem = roi.toEntity(userId);
            OrderItem saveOrderItem = orderItemRepository.save(orderItem);

            // 주문 객체에 저장
            order.getOrderItems().add(saveOrderItem);
        });






    }


}
