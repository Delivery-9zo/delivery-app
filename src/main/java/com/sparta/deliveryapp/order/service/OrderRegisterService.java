package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.order.dto.RegisterOrderItemRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderRegisterService {

    private final OrderRepository orderRepository;
    private final OrderItemRegisterService orderItemRegisterService;

    private final PaymentService paymentService;
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
    public UUID postOrder(UUID storeId,
                          RegisterOrderRequestDto registerOrderRequestDto,
                          User user) {
        log.info("주문 등록 : registerOrderRequestDto={}", registerOrderRequestDto);

        // 가게 ID로 가게 조회
        Store store = storeRepository.findByStoreId(storeId).orElseThrow(() ->
                new IllegalArgumentException("일치하는 상점이 없습니다."));

        // 메뉴 목록 조회
        List<Menu> menuList = menuRepository.findAllByStore_StoreId(store.getStoreId());

        // 주문 항목 생성 및 총 가격 계산
        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (RegisterOrderItemRequestDto itemDto : registerOrderRequestDto.getRegisterOrderItemRequestDtoList()) {
            // 해당 메뉴의 가격을 찾기
            Menu menu = menuList.stream()
                    .filter(m -> m.getId().equals(itemDto.getMenuId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));

            // 총 가격 계산
            totalPrice += Integer.parseInt(menu.getPrice().toString()) * itemDto.getQuantity();

            // 주문 항목 생성
            OrderItem orderItem = OrderItem.builder()
                    .userId(user.getUserId())
                    .menuId(menu.getId())
                    .quantity(itemDto.getQuantity())
                    .build();

            orderItems.add(orderItem); // 주문 항목 리스트에 추가
        }

        // 주문 엔티티 생성
        Order order = registerOrderRequestDto.toEntity(user.getRole() != UserRole.CUSTOMER ? null : user.getUserId(), totalPrice);
        order.setOrderState(OrderState.WAIT);
        order.setStore(store);

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        // 주문 ID 반환
        return savedOrder.getOrderId();
    }
}