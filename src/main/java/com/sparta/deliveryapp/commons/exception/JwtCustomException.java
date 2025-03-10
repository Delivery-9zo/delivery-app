package com.sparta.deliveryapp.commons.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtCustomException extends AuthenticationException {
  private final ErrorCode errorCode;

  public JwtCustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}