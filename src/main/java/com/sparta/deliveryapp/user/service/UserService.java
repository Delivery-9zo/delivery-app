package com.sparta.deliveryapp.user.service;

import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Transactional
  public void signUp(SignUpRequestDto requestDto) {

    // 이메일 중복 체크
    Optional<User> user = userRepository.findByEmail(requestDto.getUserEmail());

    if (user.isPresent()) {
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }

    // 비밀번호 암호화 할 예정
    // String password = passwordEncoder.encode(requestDto.getPassword());

    userRepository.save(new User(requestDto, requestDto.getPassword()));
  }
}
