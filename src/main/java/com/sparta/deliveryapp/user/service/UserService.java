package com.sparta.deliveryapp.user.service;

import static com.sparta.deliveryapp.commons.exception.ErrorCode.ACCESS_DENIED;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.EMAIL_ALREADY_REGISTERED;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.PASSWORD_NOT_MATCH;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.USER_DELETED;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.USER_NOT_FOUND;

import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.menu.repository.MenuRepository;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import com.sparta.deliveryapp.order.repository.OrderItemRepository;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.payment.entity.Payment;
import com.sparta.deliveryapp.payment.repository.PaymentRepository;
import com.sparta.deliveryapp.review.repository.ReviewRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import com.sparta.deliveryapp.util.NullAwareBeanUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OrderRepository orderRepository;
  private final ReviewRepository reviewRepository;
  private final OrderItemRepository orderItemRepository;
  private final MenuRepository menuRepository;
  private final PaymentRepository paymentRepository;
  private final StoreRepository storeRepository;
  private final JwtUtil jwtUtil;


  @Transactional
  public void signUp(SignUpRequestDto requestDto) {
    // 이메일 중복 체크
    Optional<User> user = userRepository.findByEmail(requestDto.getUserEmail());

    if (user.isPresent()) {
      throw new CustomException(EMAIL_ALREADY_REGISTERED);
    }

    // 비밀번호 암호화 할 예정
    String password = passwordEncoder.encode(requestDto.getPassword());

    userRepository.save(new User(requestDto, password));
  }

  public String signIn(SignInRequestDto requestDto) {
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (user.getDeletedAt() != null) {
      throw new CustomException(USER_DELETED);
    }

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new CustomException(PASSWORD_NOT_MATCH);
    }

    String token = jwtUtil.createToken(user.getEmail(), user.getRole());

    return token;
  }

  @Transactional
  public void updateUser(String email, UserUpdateRequestDto requestDto, User user) {
    User findUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (findUser.getDeletedAt() != null) {
      throw new CustomException(USER_DELETED);
    }

    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new CustomException(ACCESS_DENIED);
    }

    // 비밀번호 처리 (null이 아니면 암호화)
    if (requestDto.getPassword() != null) {
      String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
      findUser.setPassword(encodedPassword);
    }

    // requestDto의 null을 제외한 필드만 복사
    NullAwareBeanUtils.copyNonNullProperties(requestDto, findUser);

    // 업데이트된 사용자 저장
    userRepository.save(findUser);
  }

  @Transactional
  public void deleteUser(String email, User user) {
    User findUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new CustomException(ACCESS_DENIED);
    }

    String deleteBy = getCurrentUserEmail();
    userRepository.deleteUser(deleteBy, user.getUserId());

    // 주문 및 주문 상세 삭제 TODO: N + 1 문제 또는 쿼리 최적화 필요함
    orderRepository.findAllByUserId(findUser.getUserId()).forEach(order -> {
      orderItemRepository.findAllByOrderId(order.getOrderId()).forEach(orderItem ->
          orderItemRepository.deleteOrderItem(deleteBy, orderItem.getItemId())
      );

      orderRepository.deleteOrder(deleteBy, order.getOrderId());
    });

    // 결제 삭제
    paymentRepository.deletePaymentByUserId(deleteBy,findUser.getUserId());

    // 상점 메뉴 삭제
    List<Store> storeList = storeRepository.findAllByUser(findUser);
    for(Store store : storeList){
      menuRepository.softDeleteMenu(store.getStoreId(),deleteBy);
    }
    storeRepository.deleteStoreByUserId(deleteBy,findUser.getUserId());

    // 리뷰 삭제
    reviewRepository.deleteReviewByUser(deleteBy, findUser.getUserId());

    // 유저 삭제
    userRepository.deleteUser(deleteBy, user.getUserId());

  }


  public UserResponseDto getUser(String email, User user) {

    if (!user.getEmail().equals(email)) {
      throw new CustomException(ACCESS_DENIED);
    }

    if (user.getDeletedAt() != null) {
      throw new CustomException(USER_DELETED);
    }

    return new UserResponseDto(user);

  }

  // Soft Delete 180일 지난 데이터 완전 삭제
  public void cleanupDeletedUsers() {
    LocalDateTime minusDays = LocalDateTime.now().minusDays(180);
    List<User> usersToDelete = userRepository.findAllDeletedBefore(minusDays);

    if (!usersToDelete.isEmpty()) {
      userRepository.deleteAllDeletedBefore(minusDays);
      log.info("{}개의 유저 데이터가 삭제되었습니다.", usersToDelete.size());
    }
  }

  private String getCurrentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
      return ((UserDetailsImpl) auth.getPrincipal()).getEmail();
    }
    throw new SecurityException("No authenticated user found");
  }

}
