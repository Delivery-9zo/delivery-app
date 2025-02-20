package com.sparta.deliveryapp.user;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignInResponseDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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

}
