package com.sparta.deliveryapp.user.service;


import static com.sparta.deliveryapp.commons.exception.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.USER_NOT_FOUND;

import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.jwt.JwtUtil;
import com.sparta.deliveryapp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefreshTokenService {
  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;

  @Autowired
  public RefreshTokenService(JwtUtil jwtUtil, RedisTemplate<String,String> redisTemplate, UserRepository userRepository){
    this.jwtUtil = jwtUtil;
    this.redisTemplate = redisTemplate;
    this.userRepository = userRepository;
  }

  public String refreshAccessToken(String email, String refreshToken){
    // 1. 리프레시 토큰 유효성 검증
    if(!jwtUtil.validateToken(refreshToken)){
      throw new CustomException(EXPIRED_REFRESH_TOKEN);
    }

    // 추가. 블랙리스트
    // 블랙리스트 체크
    if (isTokenBlacklisted(refreshToken)) {
      throw new CustomException(INVALID_REFRESH_TOKEN);
    }

    // 2. Redis에 저장된 리프레시 토큰 확인
    String storedRefreshToken = redisTemplate.opsForValue().get("refreshToken:" + email);
    log.info(storedRefreshToken);
    if(storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
      throw new CustomException(INVALID_REFRESH_TOKEN);
    }

    // 3. 새로운 엑게스 토큰 발급
    User user = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(USER_NOT_FOUND));
    String newToken = jwtUtil.createToken(email, user.getRole());

    return newToken;
  }

  public boolean isTokenBlacklisted(String refreshToken) {
    String token = refreshToken.substring(7);  // Bearer " 제거
    return redisTemplate.hasKey("blacklist:" + token);  // 블랙리스트에 존재하면 true
  }
}
