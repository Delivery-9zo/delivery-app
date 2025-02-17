package com.sparta.deliveryapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDto {

  private String message;
  private String token;

}
