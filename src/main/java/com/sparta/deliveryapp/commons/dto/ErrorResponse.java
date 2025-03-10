package com.sparta.deliveryapp.commons.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.deliveryapp.commons.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private int status;
  private String code;
  private String message;

  public ErrorResponse(ErrorCode errorCode, String message) {
    this.status = errorCode.getStatus().value();
    this.code = errorCode.getCode();
    this.message = message;
  }

  public ErrorResponse(ErrorCode errorCode) {
    this(errorCode, errorCode.getMessage());
  }
}
