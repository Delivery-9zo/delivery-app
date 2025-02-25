package com.sparta.deliveryapp.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {

  @NotNull(message = "User Email cannot be null")
  @Schema(description = "유저 이메일",example = "master@test.com")
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  @Schema(description = "유저 비밀번호",example = "1234")
  private String password;

}