package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.order.controller.OrderRequestDto;
import com.sparta.deliveryapp.order.dto.OrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderRespository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRespository orderRespository;

    public OrderResponseDto updateOrderStateToCancel(UUID orderId, OrderRequestDto orderRequestDto) {

        logger.info("주문 상태 ToCancel 업데이트 시작 : orderId={}", orderId);

        try {
            Order order = orderRespository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 주문정보가 없습니다."));

            // 주문과 주문상세 1:N 관계를 설정 추가 필요

            if (orderRequestDto.getOrderState() == null) {
                throw new IllegalArgumentException("주문 상태가 필요합니다.");
            } else if (!orderRequestDto.getOrderState().equals(OrderState.WAIT)) {
                throw new IllegalArgumentException("주문대기 상태가 아니므로 주문수정이 불가합니다.");
            }
            // user PR 후, 권한 확인
            //else if() {
            //    throw new AccessDeniedException("주문 상태를 수정할 권한이 없습니다.");
            //}

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime orderTimeFiveMinutesLater = orderRequestDto.getOrderTime().plusMinutes(5);
            if (now.isAfter(orderTimeFiveMinutesLater)) {
                throw new IllegalArgumentException("주문 생성 5분 이내에만 취소 가능합니다.");
            } else {
                // 5분 이내
                order.setOrderState(OrderState.CANCEL);
                logger.info("주문 상태 CANCEL 로 업데이트: orderId={}", orderId);
            }

            Order updatedOrder = orderRespository.save(order);
            logger.info("주문 상태 ToCancel 업데이트 성공: orderId={}", updatedOrder.getOrderId());

            return new OrderResponseDto(updatedOrder);
        } catch (ResponseStatusException e) {
            logger.error("주문 상태 업데이트 오류: {}", e.getReason());
            throw e; // 예외를 다시 던져서 컨트롤러에서 처리하게 함
        } catch (Exception e) {
            logger.error("오류 발생: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생", e);
        } finally {
            logger.info("주문 상태 ToCancel 업데이트 종료: orderId={}", orderId);
        }
    }
}
