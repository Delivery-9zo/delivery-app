package com.sparta.deliveryapp.user.jwt;

import com.sparta.deliveryapp.user.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;
  private final RedisTemplate<String, String> redisTemplate;

  public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, RedisTemplate<String, String> redisTemplate) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.redisTemplate = redisTemplate;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

    String tokenValue = jwtUtil.getJwtFromHeader(req);

    if (StringUtils.hasText(tokenValue)) {

      if (!jwtUtil.validateToken(tokenValue)) {
        log.error("Token Error");
        return;
      }

      Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

      // 블랙리스트 검증: Access Token이 블랙리스트에 있는지 확인
      if (isTokenBlacklisted(tokenValue)) {
        log.error("Token is blacklisted");
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has been invalidated"); // 강제 로그아웃 처리
        return;
      }

      try {
        setAuthentication(info.getSubject());
      } catch (Exception e) {
        log.error(e.getMessage());
        return;
      }
    }

    filterChain.doFilter(req, res);
  }

  // 인증 처리
  public void setAuthentication(String email) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(email);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  // 인증 객체 생성
  private Authentication createAuthentication(String email) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  // 블랙리스트 검증 메소드
  private boolean isTokenBlacklisted(String tokenValue) {
    // Access Token이 블랙리스트에 있는지 확인
    return redisTemplate.opsForValue().get("blacklist:" + tokenValue) != null;
  }
}
