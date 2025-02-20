package com.sparta.deliveryapp.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
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
  @DisplayName("회원가입시 유저가 존재할 경우")
  void signUp_whenEmailAlreadyExists_throwsException() {
    SignUpRequestDto requestDto = SignUpRequestDto.builder()
        .userName("John Doe")
        .nickName("johnny")
        .password("password123")
        .role(UserRole.CUSTOMER)
        .address("123 Street, City")
        .userEmail("john.doe@example.com")
        .build();

    // 이메일 중복 처리
    when(userRepository.findByEmail(requestDto.getUserEmail())).thenReturn(Optional.of(new User()));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signUp(requestDto);
    });

    assertEquals("이미 등록된 이메일입니다.", exception.getMessage());
  }

  @Test
  @DisplayName("유저 회원가입 성공")
  void signUp_whenEmailIsValid_savesUser() {
    SignUpRequestDto requestDto = SignUpRequestDto.builder()
        .userName("John Doe")
        .nickName("johnny")
        .password("password123")
        .role(UserRole.CUSTOMER)
        .address("123 Street, City")
        .userEmail("john.doe@example.com")
        .build();

    when(userRepository.findByEmail(requestDto.getUserEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");

    userService.signUp(requestDto);

    verify(userRepository).save(any(User.class)); // 사용자 저장 호출 확인
  }


  @Test
  @DisplayName("회원이 존재하지 않는 경우 예외처리")
  void signIn_whenUserNotFound_throwsException(){
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "password123");

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signIn(requestDto);
    });

    assertEquals("회원이 존재하지 않습니다.", exception.getMessage());

  }

  @Test
  @DisplayName("로그인시 비밀번호가 다를때 예외처리")
  void signIn_whenInvalidPassword_throwsException() {
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "wrongPassword");
    User user = new User( UUID.randomUUID(),"test", "nickname", "1234","testaddress","test@test.com",UserRole.CUSTOMER);

    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.signIn(requestDto);
    });

    assertEquals("비밀번호가 다릅니다.", exception.getMessage());
  }

  @Test
  @DisplayName("로그인시 토큰 생성")
  void signIn_whenCredentialsAreValid_returnsToken() {

    // given
    SignInRequestDto requestDto = new SignInRequestDto("test@example.com", "password123");
    User user = new User( UUID.randomUUID(),"test", "nickname", "1234","testaddress","test@test.com",UserRole.CUSTOMER);

    // when
    when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtUtil.createToken(user.getEmail(), user.getRole())).thenReturn("jwt-token");

    // 서비스 로직에서 로그인시 토큰을 생성해 주기 때문에 검사
    String token = userService.signIn(requestDto);

    assertEquals("jwt-token", token);
  }

}
