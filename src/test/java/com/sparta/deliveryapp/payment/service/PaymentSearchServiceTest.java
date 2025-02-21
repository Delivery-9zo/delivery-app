package com.sparta.deliveryapp.payment.service;

import com.sparta.deliveryapp.payment.dto.PaymentResponseDto;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.entity.PaymentStatus;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DisplayName("[Payment] - 비즈니스 로직")
public class PaymentSearchServiceTest {

    private PaymentSearchService paymentSearchService;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UUID paymentId = UUID.randomUUID();
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentSearchService = new PaymentSearchService(paymentRepository, userRepository);

        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setRole(UserRole.CUSTOMER);
        user.setUserName("본명");
        user.setEmail("test@test.com");
        user.setPassword("1234");
        user.setNickName("test");
        user.setUserAddress("address-test");
    }

    @Test
    @DisplayName("결제 내역이 없는 경우, 예외를 던진다.")
    public void givenNonexistentPaymentId_whenSearchingPaymentWithPaymentId_thenThrowException() {
        // given
        paymentId = UUID.randomUUID();
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        //
        // when & then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            paymentSearchService.getPaymentById(paymentId, user); // 결제 조회 호출
        });
        // 메시지 검증
        Assertions.assertEquals("일치하는 결제 내역이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제한 회원이 없는 경우, 예외를 던진다.")
    public void givenNonexistentUserId_whenSearchingPaymentWithPaymentId_thenThrowException() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(new Payment(paymentId, orderId, userId, PaymentStatus.SUCCESS, 12900, LocalDateTime.now())));
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
        // when & then
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            paymentSearchService.getPaymentById(paymentId, user);
        });
        Assertions.assertEquals("결제한 회원이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("CUSTOMER 권한이 없는 경우, 예외를 던진다.")
    public void givenNonexistentUser_whenSearchingPayment_thenThrowException() {
        // given
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(new Payment(
            paymentId,
            orderId,
            userId,
            PaymentStatus.SUCCESS,
            12900,
            LocalDateTime.now()
        )));
        User user = new User();
        user.setUserId(userId);
        user.setRole(UserRole.MASTER);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // when & then
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class, () -> {
            paymentSearchService.getPaymentById(paymentId, user);
        });
        // 메시지 검증
        Assertions.assertEquals("CUSTOMER 권한을 가진 사용자만 조회할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 userID 와 로그인된 userId 불일치 시, 예외를 던진다.")
    public void givenNonMatchingUserId_whenSearchingPayment_thenThrowException() {
        // given
        UUID otherUserId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Payment payment = new Payment(
            paymentId,
            orderId,
            otherUserId,
            PaymentStatus.SUCCESS,
            12900,
            LocalDateTime.now());
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        Mockito.when(userRepository.findById(payment.getUserId())).thenReturn(Optional.of(new User(
            orderId,
            user.getUserName(),
            user.getNickName(),
            user.getPassword(),
            user.getUserAddress(),
            user.getEmail(),
            UserRole.MASTER
        )));
        // user 객체의 userId를 payment.getUserId와 다르게 설정
        user.setUserId(UUID.randomUUID());
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(new User(
            orderId,
            user.getUserName(),
            user.getNickName(),
            user.getPassword(),
            user.getUserAddress(),
            user.getEmail(),
            UserRole.MASTER
        )));
        // when & then
        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            paymentSearchService.getPaymentById(paymentId, user);
        });

        // 메시지 검증
        Assertions.assertEquals("본인의 결제 정보만 조회 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 내역을 1건 조회할 수 있다.")
    public void givenExistingPaymentId_whenSearchingPayment_thenReturnPaymentResponseDto() {
        // given
        UUID paymentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        // User 객체 설정
        User user = new User();
        user.setUserId(userId);
        user.setRole(UserRole.CUSTOMER);

        // Payment 객체 설정
        Payment payment = new Payment(
            paymentId,
            orderId,
            userId,
            PaymentStatus.SUCCESS,
            12900,
            LocalDateTime.now());

        // 모킹 설정
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        PaymentResponseDto responseDto = paymentSearchService.getPaymentById(paymentId, user);

        // then
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(paymentId, responseDto.getPaymentId());
        Assertions.assertEquals(orderId, responseDto.getOrderId());
        Assertions.assertEquals(userId, responseDto.getUserId());
        Assertions.assertEquals(PaymentStatus.SUCCESS, responseDto.getPaymentStatus());
        Assertions.assertEquals(12900, responseDto.getPaymentAmount());
    }

}
