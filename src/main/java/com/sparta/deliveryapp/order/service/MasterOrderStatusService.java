package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterOrderStatusService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 취소(SUCCESS -> CANCEL) : MASTER
    // 주문 취소되면 결제도 취소됨
    @Transactional
    public Order deleteOrderMaster(UUID orderId, UUID storeId) {

        log.info("주문 취소 시작 : orderId={}", orderId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));


        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_STORE_ID));

        if (order.getOrderState() != OrderState.SUCCESS) {
            throw new CustomException(ErrorCode.ORDER_STATUS_FAILED_ORDER);
        }

        // 주문상세 List
        List<OrderItem> orderItemsList = orderItemRepository.findByOrder(order);

        // 결제 paymentId = order.getPayment().getPaymentId();
        Payment payment = paymentRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_PAYMENT_ID));

        // 주문&결제 userId 추출
        UUID fixUserId = payment.getUserId();

        // 주문 취소 조건(5분 이내 가능) 및 삭제 처리(주문->주문상세->결제 삭제)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderTimeFiveMinutesLater = order.getOrderTime().plusMinutes(5);
        if (now.isAfter(orderTimeFiveMinutesLater)) {
            log.error("validOrderTime 메소드 - 주문취소 불가");
            throw new CustomException(ErrorCode.AFTER_FIVE_ORDER_STATUS_FAILED_ORDER);
        } else {
            // 5분 이내
            // 주문상태 CANCEL 변경 및 저장
            order.setOrderState(OrderState.CANCEL);
            order.setUserId(fixUserId);
            Order completedOrder = orderRepository.save(order);


            // 주문 소프트 삭제
            String deletedBy = getCurrentUserEmail();

            orderRepository.deleteOrder(deletedBy, order.getOrderId());

            // 주문 상세 소프트 삭제
            for (OrderItem orderItem : orderItemsList) {
                orderItemRepository.deleteOrderItem(deletedBy, orderItem.getItemId());
            }

            // 결제 상태 CANCEL 변경 및 저장
            Payment deletedPayment = Payment.builder()
                    .userId(order.getUserId())
                    .orderId(payment.getOrderId())
                    .paymentStatus(PaymentStatus.CANCEL)
                    .paymentId(payment.getPaymentId())
                    .paymentAmount(payment.getPaymentAmount())
                    .paymentId(payment.getPaymentId())
                    .build();
            paymentRepository.save(deletedPayment);

            // 결제 소프트 삭제
            paymentRepository.deletePayment(deletedBy, deletedPayment.getPaymentId());

            log.info("주문 취소 종료 : orderId={}", completedOrder.getOrderId());

            return completedOrder;
        }
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        throw new SecurityException("No authenticated user found.");
    }
}
