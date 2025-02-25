package com.sparta.deliveryapp.user.dto;

import com.sparta.deliveryapp.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

  @NotEmpty(message = "User name cannot be empty")
  @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters")
  @Schema(description = "유저 이름",example = "마스터 유저 이름")
  private String userName;

  @NotEmpty(message = "Nick name cannot be empty")
  @Schema(description = "유저 닉네임",example = "마스터 유저 닉네임")
  private String nickName;

  @NotEmpty(message = "Password cannot be empty")
  @Schema(description = "비밀번호",example = "1234")
  private String password;

  @NotNull(message = "Role cannot be null")
  @Schema(description = "유저 권한",example = "MASTER")
  private UserRole role;

  @Schema(description = "유저 주소",example = "황새울로234 104")
  private String address;

  @NotEmpty(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  @Schema(description = "유저 이메일",example = "master@test.com")
  private String userEmail;
}

