package com.sparta.deliveryapp.user.service;

import com.sparta.deliveryapp.ai.AI;
import com.sparta.deliveryapp.ai.AIRepository;
import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AIRepository aiRepository;
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
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 다릅니다.");
    }

    String token = jwtUtil.createToken(user.getEmail(), user.getRole());

    return token;
  }

  @Transactional
  public void updateUser(String email, UserUpdateRequestDto requestDto, User user) {
    User findUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));


    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new AccessDeniedException("사용자 정보를 수정할 권한이 없습니다.");
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
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

    if (!findUser.getUserId().equals(user.getUserId())) {
      throw new AccessDeniedException("사용자 정보를 수정할 권한이 없습니다.");
    }

    userRepository.delete(findUser);
    findUser.onPreRemove();

    // ai 데이터도 삭제
    List<AI> listAi = aiRepository.findByUser(user);
    if(listAi != null){
      aiRepository.deleteAll(listAi);
    }

    // TODO: 연관되어 있는 리뷰,주문,상점도 soft delete 삭제 로직 추가
  }

  public UserResponseDto getUser(String email, User user) {

    if (!user.getEmail().equals(email)) {
      throw new AccessDeniedException("접근 권한이 없습니다.");
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
}
