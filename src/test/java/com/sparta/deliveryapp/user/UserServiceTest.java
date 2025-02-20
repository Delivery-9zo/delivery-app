package com.sparta.deliveryapp.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository, passwordEncoder, jwtUtil);
  }

  @Test
  @DisplayName("회원가입 시 유저가 존재할 경우 예외 처리")
  void givenEmailAlreadyExists_whenSignUp_thenThrowsException() {
    // given
    SignUpRequestDto requestDto = SignUpRequestDto.builder()
        .userName("John Doe")
        .nickName("johnny")
        .password("password123")
        .role(UserRole.CUSTOMER)
        .address("123 Street, City")
        .userEmail("john.doe@example.com")
        .build();

    // when
    when(userRepository.findByEmail(requestDto.getUserEmail())).thenReturn(Optional.of(new User()));

    // then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signUp(requestDto);
    });

    assertEquals("이미 등록된 이메일입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("유저 회원가입 성공")
  void givenValidEmail_whenSignUp_thenSavesUser() {
    // given
    SignUpRequestDto requestDto = SignUpRequestDto.builder()
        .userName("John Doe")
        .nickName("johnny")
        .password("password123")
        .role(UserRole.CUSTOMER)
        .address("123 Street, City")
        .userEmail("john.doe@example.com")
        .build();

    // when
    when(userRepository.findByEmail(requestDto.getUserEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");

    // then
    userService.signUp(requestDto);
    verify(userRepository).save(any(User.class)); // 사용자 저장 호출 확인
  }

  @Test
  @DisplayName("회원이 존재하지 않는 경우 예외 처리")
  void givenNonExistentUser_whenSignIn_thenThrowsException() {
    // given
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "password123");

    // when
    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

    // then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signIn(requestDto);
    });

    assertEquals("회원이 존재하지 않습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("로그인 시 비밀번호가 다를 때 예외 처리")
  void givenInvalidPassword_whenSignIn_thenThrowsException() {
    // given
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "wrongPassword");
    User user = new User(UUID.randomUUID(), "test", "nickname", "1234", "testaddress",
        "test@test.com", UserRole.CUSTOMER);

    // when
    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);

    // then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signIn(requestDto);
    });

    assertEquals("비밀번호가 다릅니다.", exception.getMessage());
  }

  @Test
  @DisplayName("로그인 시 토큰 생성")
  void givenValidCredentials_whenSignIn_thenReturnsToken() {
    // given
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "password123");
    User user = new User(UUID.randomUUID(), "test", "nickname", "1234", "testaddress",
        "test@test.com", UserRole.CUSTOMER);

    // when
    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtUtil.createToken(user.getEmail(), user.getRole())).thenReturn("jwt-token");

    // then
    String token = userService.signIn(requestDto);
    assertEquals("jwt-token", token);
  }

  @Test
  @DisplayName("유저 업데이트 시 null이 들어가도 기존 데이터와 같은지")
  void givenNullPassword_whenUpdateUser_thenPreservesOldPassword() {
    // given
    User user = new User(UUID.randomUUID(), "test", "nickname", "1234", "testaddress",
        "test@test.com", UserRole.CUSTOMER);
    User equalUser = new User(user.getUserId(), "test", "nickname", "1234", "testaddress",
        "test@test.com", UserRole.CUSTOMER);

    // when
    UserUpdateRequestDto requestDto = new UserUpdateRequestDto("수정한 이름", null, null, null);
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(equalUser));
    userService.updateUser("test@test.com", requestDto, user);

    // then
    verify(userRepository).save(equalUser);
    assertEquals(equalUser.getPassword(), user.getPassword()); // 비밀번호가 업데이트되지 않았는지 확인
    assertNotEquals(equalUser.getUserName(), user.getUserName());
    assertEquals(equalUser.getNickName(), user.getNickName());
  }

  @Test
  @DisplayName("삭제 시 소프트 삭제가 이루어졌는지")
  void givenUserToDelete_whenDeleteUser_thenSoftDeletes() {
    // given
    User user = new User(UUID.randomUUID(), "test", "nickname", "1234", "testaddress",
        "test@test.com", UserRole.CUSTOMER);

    // SecurityContext 강제 설정
    Authentication authentication = new UsernamePasswordAuthenticationToken("test@test.com", "password",
        AuthorityUtils.createAuthorityList("ROLE_CUSTOMER"));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // when
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    userService.deleteUser("test@test.com", user);

    // then
    assertNotNull(user.getDeletedAt());
    assertNotEquals(user.getDeletedAt(), null); // deletedAt에 값이 들어가 있는지 확인
    verify(userRepository).save(user);
  }

}
