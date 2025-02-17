package com.sparta.deliveryapp.user.service;

import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Transactional
  public void signUp(SignUpRequestDto requestDto) {
    // 이메일 중복 체크
    Optional<User> user = userRepository.findByEmail(requestDto.getUserEmail());

    if (user.isPresent()) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    // 비밀번호 암호화 할 예정
    String password = passwordEncoder.encode(requestDto.getPassword());

    userRepository.save(new User(requestDto, password));
  }

  public String signIn(SignInRequestDto requestDto) {
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("없는 유저 입니다."));

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 다릅니다.");
    }

    String token = jwtUtil.createToken(user.getEmail(),user.getRole());

    return token;
  }

  @Transactional
  public void updateUser(String email,UserUpdateRequestDto requestDto, User user) {
    User findUser = userRepository.findByEmail(email)
        .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다."));

    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new AccessDeniedException("사용자 정보를 수정할 권한이 없습니다.");
    }

    String password = passwordEncoder.encode(requestDto.getPassword());

    findUser.update(requestDto,password);
    userRepository.save(findUser);
  }

  @Transactional
  public void deleteUser(String email, User user) {
    User findUser = userRepository.findByEmail(email)
        .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다."));

    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new AccessDeniedException("사용자 정보를 수정할 권한이 없습니다.");
    }

    findUser.delete();

    userRepository.save(findUser);
  }
}
