package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.dto.UpdateOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;

    // 주문 취소되면 결제도 취소됨
    // 주문 취소(SUCCESS -> CANCEL) : MANAGER,OWNER
    @Transactional
    public Order deleteOrder(UUID orderId, UUID storeId, User user) {

        log.info("주문 취소 시작 : orderId={}", orderId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_ORDER_ID));

        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_STORE_ID));

        if (order.getOrderState() != OrderState.SUCCESS) {
            throw new CustomException(ErrorCode.ORDER_STATUS_FAILED_ORDER);
        }

        // 주문상세 List
        List<OrderItem> orderItemsList = orderItemRepository.findByOrderId(order);

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

            // 결제 상태 CANCEL 변경
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

            log.info("주문 취소 종료 orderId={}", completedOrder.getOrderId());

            return completedOrder;
        }
    }

    // 주문 취소(SUCCESS -> CANCEL) : CUSTOMER
    @Transactional
    public Order deleteOrderCustomer(UUID orderId, UUID storeId, User user) {

        log.info("주문 취소 시작 orderId={}", orderId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 주문정보가 없습니다."));

        // CUSTOMER userId 동일여부 체크
        if(user.getUserId() != order.getUserId()) {
            throw new IllegalArgumentException("해당 주문결제 고객이 아닙니다.");
        }

        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 가게가 없습니다."));

        if (order.getOrderState() != OrderState.SUCCESS) {
            throw new IllegalArgumentException("주문완료 상태가 아니므로 주문취소가 불가능합니다.");
        }

        // 주문상세 List
        List<OrderItem> orderItemsList = orderItemRepository.findByOrderId(order);

        // 결제 paymentId = order.getPayment().getPaymentId();
        Payment payment = paymentRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 결제 내역이 없습니다."));

        // 주문&결제 userId 추출
        UUID fixUserId = payment.getUserId();

        // 주문 취소 조건(5분 이내 가능) 및 삭제 처리(주문->주문상세->결제 삭제)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderTimeFiveMinutesLater = order.getOrderTime().plusMinutes(5);
        if (now.isAfter(orderTimeFiveMinutesLater)) {
            log.error("validOrderTime 메소드 - 주문취소 불가");
            throw new IllegalArgumentException("주문취소는 주문완료 후 5분 이내에만 취소 가능합니다.");
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

            // 결제 상태 CANCEL 변경
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

    // 비대면 주문완료 - 상태수정(SUCCESS) -> 결제 등록(SUCCESS) : CUSTOMER
    public UpdateOrderResponseDto updateOrderNotFace(UUID orderId, User user) {

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 가능한 주문내역이 존재하지 않습니다.")
        );

        if(order.getOrderType() != OrderType.NON_FACE_TO_FACE) {
            throw new IllegalArgumentException("배달 주문만 결제 가능합니다. 대면 주문과 결제는 가게에 문의해주세요!");
        }

        checkOrderState(order);

        // 결제 요청 dto 생성
        RegisterPaymentRequestDto paymentRequestDto = RegisterPaymentRequestDto.builder()
                .userId(order.getUserId())
                .orderId(order.getOrderId())
                .paymentAmount(order.getTotalPrice())
                .build();


        // 결제 등록 서비스 호출
        try {
            RegisterPaymentResponseDto registerPaymentResponseDto = paymentService.postPayment(paymentRequestDto, user);
            if(registerPaymentResponseDto.getPaymentStatus() != PaymentStatus.SUCCESS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 중 오류가 발생했습니다.");
            }

        } catch (Exception e) {
            log.error("오류 발생={}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.");
        }

        // 주문 상태 SUCCESS 로 업데이트 및 저장
        order.setOrderState(OrderState.SUCCESS);
        order.setTotalPrice(order.getTotalPrice());
        Order completedOrder = orderRepository.save(order);

        log.info("비대면 주문등록 서비스 종료");
        return UpdateOrderResponseDto.builder()
                .orderId(completedOrder.getOrderId())
                .userId(completedOrder.getUserId())
                .orderState(completedOrder.getOrderState())
                .totalPrice(completedOrder.getTotalPrice())
                .orderType(completedOrder.getOrderType())
                .build();
    }

    // 대면 주문완료 - 상태수정(SUCCESS) -> 결제 등록(SUCCESS) : MANAGER, OWNER
    public UpdateOrderResponseDto updateOrderFace(UUID orderId, User user) {

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 가능한 주문내역이 존재하지 않습니다.")
        );

        if(order.getOrderType() != OrderType.FACE_TO_FACE) {
            throw new IllegalArgumentException("비대면 주문과 결제는 CUSTOMER 만 가능합니다.");
        }

        checkOrderState(order);

        // 결제 요청 dto 생성
        RegisterPaymentRequestDto paymentRequestDto = RegisterPaymentRequestDto.builder()
                .orderId(order.getOrderId())
                .paymentAmount(order.getTotalPrice())
                .userId(user.getRole() == UserRole.MANAGER || user.getRole() == UserRole.OWNER ? null : order.getUserId())
                .build();

        // 결제 등록 서비스 호출
        try {
            RegisterPaymentResponseDto registerPaymentResponseDto = paymentService.postPayment(paymentRequestDto, user);
            if(registerPaymentResponseDto.getPaymentStatus() != PaymentStatus.SUCCESS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 중 오류가 발생했습니다.");
            }

        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.");
        }

        // 주문 상태 SUCCESS 로 업데이트 및 저장
        order.setOrderState(OrderState.SUCCESS);
        order.setTotalPrice(order.getTotalPrice());
        Order completedOrder = orderRepository.save(order);

        log.info("대면 주문등록 서비스 종료");
        return UpdateOrderResponseDto.builder()
                .orderId(completedOrder.getOrderId())
                .userId(completedOrder.getUserId())
                .orderState(completedOrder.getOrderState())
                .totalPrice(completedOrder.getTotalPrice())
                .orderType(completedOrder.getOrderType())
                .build();
    }

    private void checkOrderState(Order order) {
        if(order.getOrderState() != OrderState.WAIT) {
            throw new IllegalArgumentException("주문완료 및 취소 상태로 주문완료가 불가능합니다.");
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
