package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentRequestDto;
import com.sparta.deliveryapp.payment.dto.RegisterPaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderRegisterService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final PaymentService paymentService;

    @Transactional
    public RegisterOrderResponseDto postOrder(RegisterOrderRequestDto registerOrderRequestDto, User user) {
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

        // 결제 요청 Dto 생성
        RegisterPaymentRequestDto paymentRequestDto = new RegisterPaymentRequestDto();
        paymentRequestDto.setOrderId(saveOrder.getOrderId());
        paymentRequestDto.setOrderType(saveOrder.getOrderType());
        paymentRequestDto.setOrderState(saveOrder.getOrderState());
        paymentRequestDto.setPaymentAmount(totalPrice);

        // 결제 서비스 호출
        log.info("주문등록 서비스에서 결제 서비스 호출 전 : orderId={}", order.getOrderId());
        log.info("주문 상태={}", order.getOrderState());

        try {
            RegisterPaymentResponseDto registerPaymentResponseDto = paymentService.postPayment(paymentRequestDto, user);
            if (registerPaymentResponseDto.getPaymentStatus() != PaymentStatus.SUCCESS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 중 오류가 발생했습니다.");
            }

            log.info("주문 등록 서비스에서 결제 서비스 호출 후 : orderId={}", registerPaymentResponseDto.getOrderId());
            log.info("결제 상태={}", registerPaymentResponseDto.getPaymentStatus());

            // 결제 정보 설정 - Order 의  외래키
            Payment payment = new Payment();
            payment.setPaymentId(registerPaymentResponseDto.getPaymentId());
            payment.setPaymentStatus(registerPaymentResponseDto.getPaymentStatus());
            payment.setPaymentAmount(registerPaymentResponseDto.getPaymentAmount());
            payment.setPaymentTime(registerPaymentResponseDto.getPaymentTime());

            // 주문에 결제 정보 설정
            order.setPayment(payment);

        } catch (AccessDeniedException e) {
            log.error("결제 접근 거부 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ResponseStatusException e) {
            log.error("결제 서비스 오류: {}", e.getReason());
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.");
        }

        // 주문상태 "SUCCESS"로 업데이트 및 저장
        order.setOrderState(OrderState.SUCCESS);
        Order saveCompletedOrder = orderRepository.save(order);

        log.info("주문등록 서비스 종료 : orderId={}", saveCompletedOrder.getOrderId());
        return RegisterOrderResponseDto.builder()
            .orderId(saveCompletedOrder.getOrderId())
            .orderState(saveCompletedOrder.getOrderState())
            .build();
    }

    //총 금액 계산
    public int calculateTotalPrice(int price, int quantity) {
        return price * quantity;
    }

}
