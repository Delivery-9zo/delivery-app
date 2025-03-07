package com.sparta.deliveryapp.user.jwt;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.JwtCustomException;
import com.sparta.deliveryapp.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // 사용자 권한 값의 KEY
  public static final String AUTHORIZATION_KEY = "auth";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // 토큰 만료시간
  private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분


  @Value("${spring.secrets.secretKey}")
  private String secretKey;

  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // 토큰 생성
  public String createToken(String email, UserRole role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(email)
            .claim(AUTHORIZATION_KEY, role.name())
            .setExpiration(new Date(date.getTime() + TOKEN_TIME))
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  // header 에서 JWT 가져오기
  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  // 토큰 검증
  public void validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
      throw new JwtCustomException(ErrorCode.INVALID_TOKEN);
    } catch (ExpiredJwtException e) {
      log.warn("Expired JWT token, 만료된 JWT token 입니다.");
      throw new JwtCustomException(ErrorCode.EXPIRED_TOKEN);
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
      throw new JwtCustomException(ErrorCode.UNSUPPORTED_TOKEN);
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
      throw new JwtCustomException(ErrorCode.EMPTY_CLAIMS);
    }
  }

  // 리프레시 토큰 생성
  public String createRefreshToken() {
    Date date = new Date();

    // 리프레시 토큰의 유효 기간을 7일로 설정 (예시)
    long refreshTokenTime = 7 * 24 * 60 * 60 * 1000L; // 7일

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject("refreshToken") // 리프레시 토큰의 경우, 보통 이메일을 넣지 않고 고정값을 사용
            .setExpiration(new Date(date.getTime() + refreshTokenTime)) // 7일 뒤 만료
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  // 토큰에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
