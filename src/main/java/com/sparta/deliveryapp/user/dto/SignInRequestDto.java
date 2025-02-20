package com.sparta.deliveryapp.user.dto;


import io.swagger.v3.oas.annotations.Parameter;
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

  @Parameter(description = "사용자의 이메일")
  @Schema(description = "사용자의 이메일을 입력하세요")
  @NotNull(message = "User Email cannot be null")
  private String email;

  @Parameter(description = "사용자의 비밀번호")
  @Schema(description = "비밀번호을 입력하세요")
  @NotEmpty(message = "Password cannot be empty")
  private String password;

}