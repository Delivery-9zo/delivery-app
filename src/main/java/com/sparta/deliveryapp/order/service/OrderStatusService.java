package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.order.dto.CancellationOrderRequestDto;
import com.sparta.deliveryapp.order.dto.CancellationOrderResponseDto;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderRespository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRespository orderRespository;
    private final UserRepository userRepository;

    // 주문 취소(SUCCESS -> CANCEL)
    @Transactional
    public CancellationOrderResponseDto updateOrderStateToCancel(UUID orderId, CancellationOrderRequestDto cancellationOrderRequestDto, User user) {

        log.info("주문 상태 ToCancel 업데이트 시작 : orderId={}", orderId);

        try {
            Order order = orderRespository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 주문정보가 없습니다."));
            if (cancellationOrderRequestDto.getOrderState() == null) {
                throw new IllegalArgumentException("주문 상태가 필요합니다.");
            } else if (!cancellationOrderRequestDto.getOrderState().equals(OrderState.WAIT)) {
                throw new IllegalArgumentException("주문완료 상태가 아니므로 주문취소가 불가합니다.");
            }

            User findUser = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(()-> new IllegalArgumentException("주문한 회원이 존재하지 않습니다."));

            if (findUser.getRole() == UserRole.MASTER) {
                throw new AccessDeniedException("주문을 취소할 권한(MASTER)이 없습니다.");
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime orderTimeFiveMinutesLater = cancellationOrderRequestDto.getOrderTime().plusMinutes(5);
            if (now.isAfter(orderTimeFiveMinutesLater)) {
                throw new IllegalArgumentException("주문취소는 주문완료 후 5분 이내에만 취소 가능합니다.");
            } else {
                // 5분 이내
                order.setOrderState(OrderState.CANCEL);
                log.info("주문 상태 CANCEL 로 업데이트: orderId={}", orderId);
            }

            Order updatedOrder = orderRespository.save(order);
            log.info("주문 상태 ToCancel 업데이트 성공: orderId={}", updatedOrder.getOrderId());

            return new CancellationOrderResponseDto(updatedOrder);
        } catch (ResponseStatusException e) {
            log.error("주문 상태 업데이트 오류: {}", e.getReason());
            throw e; // 예외를 다시 던져서 컨트롤러에서 처리하게 함
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생", e);
        } finally {
            log.info("주문 상태 ToCancel 업데이트 종료: orderId={}", orderId);
        }
    }
}
