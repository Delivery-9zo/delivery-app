package com.sparta.deliveryapp.commons.exception;

import com.sparta.deliveryapp.user.jwt.JwtAuthorizationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {

    log.error("Authentication error: {}", authException.getMessage());

    ErrorCode errorCode = ErrorCode.ACCESS_DENIED; // 기본값
    if (authException.getCause() instanceof JwtCustomException jwtException) {
      errorCode = jwtException.getErrorCode();
    }

    JwtAuthorizationFilter.setErrorResponse(response, errorCode);
  }
}