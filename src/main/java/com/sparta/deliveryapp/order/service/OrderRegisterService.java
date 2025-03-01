package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
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
public class OrderRegisterService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 주문 등록
     *
     * @param registerOrderRequestDto
     * @param storeId,user
     * @return UUID orderId
     */
    @Transactional
    public UUID postOrder(UUID storeId, UUID menuId, int quantity,
                          RegisterOrderRequestDto registerOrderRequestDto,
                          User user) {
        log.info("주문 등록 : registerOrderRequestDto={}", registerOrderRequestDto);

        // 가게 ID로 가게 조회
        Store store = storeRepository.findByStoreId(storeId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_EXISTS_STORE_ID));

        // 메뉴 목록 조회
        List<Menu> menuList = menuRepository.findAllByStore_StoreId(store.getStoreId());

        // 주문 객체 생성 및 주문 저장
        int totalPrice = 0;
        Order order = registerOrderRequestDto.toEntity();
        order.setUserId(user.getUserId());
        order.setOrderState(OrderState.WAIT);
        order.setStore(store);
        Order savedOrder = orderRepository.save(order);

        // 메뉴 수량에 따른 계산
        Menu menu = menuList.stream()
                .filter(m -> m.getId().equals(menuId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MENU_ID));
        int itemTotalPrice = Integer.parseInt(menu.getPrice().toString());

        // 총 가격 계산
        totalPrice += itemTotalPrice * quantity;

        // 주문 항목 생성 및 저장
        OrderItem orderItem = new OrderItem(savedOrder, menu, quantity);
        orderItemRepository.save(orderItem);

        // 주문 총금액 설정 및 저장
        savedOrder.setTotalPrice(totalPrice);
        orderRepository.save(savedOrder);

        return savedOrder.getOrderId();

//
//        for (RegisterOrderItemRequestDto itemDto : registerOrderItemListRequestDto) {
//            // 해당 메뉴의 가격을 찾기
//            Menu menu = menuList.stream()
//                    .filter(m -> m.getId().equals(itemDto.getMenuId()))
//                    .findFirst()
//                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MENU_ID));
//
//            int itemTotalPrice = Integer.parseInt(menu.getPrice().toString());
//
//            // 총 가격 계산
//            totalPrice += itemTotalPrice * itemDto.getQuantity();
//
//            // 주문 항목 저장
//            OrderItem orderItem = new OrderItem(savedOrder, menu, itemDto.getQuantity());
//
//            orderItems.add(orderItem);
//        }
//
//        orderItemRepository.saveAll(orderItems);
//
//        //savedOrder.setOrderItems(orderItems);
//        savedOrder.setTotalPrice(totalPrice);
//
//        orderRepository.save(savedOrder);
//
//        // 주문 ID 반환
//        return savedOrder.getOrderId();
    }
}