package com.sparta.deliveryapp.user.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {

  @NotNull(message = "User Email cannot be null")
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  private String password;

}