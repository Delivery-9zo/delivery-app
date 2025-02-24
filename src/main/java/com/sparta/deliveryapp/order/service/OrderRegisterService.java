package com.sparta.deliveryapp.order.service;

import com.sparta.deliveryapp.order.dto.RegisterOrderRequestDto;
import com.sparta.deliveryapp.order.dto.RegisterOrderResponseDto;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.service.PaymentService;
import com.sparta.deliveryapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderRegisterService {

    private final OrderRepository orderRepository;
    private final OrderItemRegisterService orderItemRegisterService;

    private final PaymentService paymentService;

    /**
     * 주문 등록
     * @param registerOrderRequestDto
     * @param user
     * @return
     */
    @Transactional
    public RegisterOrderResponseDto postOrder(RegisterOrderRequestDto registerOrderRequestDto, User user) {
        log.info("주문 등록 : registerOrderRequestDto={}", registerOrderRequestDto);
        return null;
//
//        // 주문 수량이 0일 경우
//        if (registerOrderRequestDto.getQuantity() == 0) {
//            throw new CustomException(ErrorCode.ITEM_SOLD_OUT);
//        }
//
//        // 메뉴의 수량이 0일 경우(메뉴가 품절일 경우)
//        //TODO 메뉴 수량(메뉴 API 연동 예정, 현재는 에러나지 않도록 1로 기본값 설정)
//        int menuQuantity = 1;
//        if (menuQuantity == 0) {
//            throw new CustomException(ErrorCode.NON_ZERO_PARAMETER);
//        }
//
//        HashMap<String, Object> map = new HashMap<String, Object>();
//
//        if (registerOrderRequestDto.getOrderType().equals(OrderType.NON_FACE_TO_FACE)) {
//            // 비대면 주문
//            map = (HashMap<String, Object>) nonFaceToFaceOrderProcessing(user, registerOrderRequestDto);
//        } else if (registerOrderRequestDto.getOrderType().equals(OrderType.FACE_TO_FACE)) {
//            // 대면 주문
//            map = (HashMap<String, Object>) faceToFaceOrderProcessing(registerOrderRequestDto);
//        }
//
//        Order saveOrder = (Order) map.get("order");
//        int totalPrice = (int) map.get("totalCount");
//
//        // 결제 요청 Dto 생성
//        RegisterPaymentRequestDto paymentRequestDto = new RegisterPaymentRequestDto();
//        paymentRequestDto.setOrderId(saveOrder.getOrderId());
//        paymentRequestDto.setOrderType(saveOrder.getOrderType());
//        paymentRequestDto.setOrderState(saveOrder.getOrderState());
//        paymentRequestDto.setPaymentAmount(totalPrice);
//
//        // 결제 서비스 호출
//        log.info("주문등록 서비스에서 결제 서비스 호출 전 : orderId={}", saveOrder.getOrderId());
//        log.info("주문 상태={}", saveOrder.getOrderState());
//
//        try {
//            RegisterPaymentResponseDto registerPaymentResponseDto = paymentService.postPayment(paymentRequestDto, user);
//            if (registerPaymentResponseDto.getPaymentStatus() != PaymentStatus.SUCCESS) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 중 오류가 발생했습니다.");
//            }
//
//            log.info("주문 등록 서비스에서 결제 서비스 호출 후 : orderId={}", registerPaymentResponseDto.getOrderId());
//            log.info("결제 상태={}", registerPaymentResponseDto.getPaymentStatus());
//
//            // 결제 정보 설정 - Order 의  외래키
//            Payment payment = Payment.builder()
//                    .paymentId(registerPaymentResponseDto.getPaymentId())
//                    .paymentAmount(registerPaymentResponseDto.getPaymentAmount())
//                    .paymentStatus(registerPaymentResponseDto.getPaymentStatus())
//                    .paymentTime(registerPaymentResponseDto.getPaymentTime())
//                    .build();
//
//            // 주문에 결제 정보 설정
//            saveOrder.setPayment(payment);
//
//        } catch (AccessDeniedException e) {
//            log.error("결제 접근 거부 오류: {}", e.getMessage());
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
//        } catch (ResponseStatusException e) {
//            log.error("결제 서비스 오류: {}", e.getReason());
//            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
//        } catch (Exception e) {
//            log.error("오류 발생: {}", e.getMessage());
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.");
//        }
//
//        // 주문상태 "SUCCESS"로 업데이트 및 저장
//        saveOrder.setOrderState(OrderState.SUCCESS);
//        Order saveCompletedOrder = orderRepository.save(saveOrder);
//
//        log.info("주문등록 서비스 종료 : orderId={}", saveCompletedOrder.getOrderId());
//        return RegisterOrderResponseDto.builder()
//                .orderId(saveCompletedOrder.getOrderId())
//                .build();
//    }
//
//    /**
//     * 총 금액 계산
//     * @param price
//     * @param quantity
//     * @return
//     */
//    public int calculateTotalPrice(int price, int quantity) {
//        return price * quantity;
//    }
//
//    /**
//     * 비대면 주문 처리
//     * @param user
//     * @param registerOrderRequestDto
//     * @return
//     */
//    public Object nonFaceToFaceOrderProcessing(User user, RegisterOrderRequestDto registerOrderRequestDto) {
//
//
//        List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList = registerOrderRequestDto.getRegisterOrderItemRequestDtoList();
//        // 총 금액 계산
//        AtomicInteger totalPrice = new AtomicInteger();
//
//        registerOrderItemRequestDtoList.forEach(registerOrderItemRequestDto -> {
//            totalPrice.addAndGet(calculateTotalPrice(registerOrderItemRequestDto.getPrice(), registerOrderItemRequestDto.getQuantity()));
//        });
//
//        Order order = registerOrderRequestDto.toEntity(user.getUserId(), totalPrice.get());
//
//        orderItemRegisterService.postOrderItem(registerOrderItemRequestDtoList, order, user);
//
//        HashMap<String, Object> result = new HashMap<>();
//
//        result.put("order", orderRepository.save(order));
//        result.put("totalPrice", totalPrice);
//
//        return result;
//    }
//
//
//    /**
//     * 대면 주문 처리 (비회원 주문과 같은 개념)
//     * @param registerOrderRequestDto
//     * @return
//     */
//    public Object faceToFaceOrderProcessing(RegisterOrderRequestDto registerOrderRequestDto) {
//        List<RegisterOrderItemRequestDto> registerOrderItemRequestDtoList = registerOrderRequestDto.getRegisterOrderItemRequestDtoList();
//        // 총 금액 계산
//        AtomicInteger totalPrice = new AtomicInteger();
//
//        registerOrderItemRequestDtoList.forEach(registerOrderItemRequestDto -> {
//            totalPrice.addAndGet(calculateTotalPrice(registerOrderItemRequestDto.getPrice(), registerOrderItemRequestDto.getQuantity()));
//        });
//
//        Order order = registerOrderRequestDto.toEntity(null, totalPrice.get());
//
//        //주문 상세 등록
//        orderItemRegisterService.postOrderItem(registerOrderItemRequestDtoList, order, null);
//
//        HashMap<String, Object> result = new HashMap<>();
//
//        result.put("order", orderRepository.save(order));
//        result.put("totalPrice", totalPrice);
//
//        return result;
    }
}
