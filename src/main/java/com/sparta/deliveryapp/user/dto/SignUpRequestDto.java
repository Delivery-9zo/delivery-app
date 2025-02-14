package com.sparta.deliveryapp.user.dto;

import com.sparta.deliveryapp.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

  @NotEmpty(message = "User name cannot be empty")
  @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters")
  private String userName;

  @NotEmpty(message = "Nick name cannot be empty")
  private String nickName;

  @NotEmpty(message = "Password cannot be empty")
  private String password;

  @NotNull(message = "Role cannot be null")
  private UserRole role;

  private String address;

  @NotEmpty(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  private String userEmail;
}

