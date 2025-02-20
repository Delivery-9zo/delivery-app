package com.sparta.deliveryapp.user.dto;

import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
  private String userName;
  private String nickName;
  private String userAddress;
  private String email;
  private UserRole role;

  public UserResponseDto(User user){
    this.userName = user.getUserName();
    this.nickName = user.getNickName();
    this.userAddress = user.getUserAddress();
    this.email = user.getEmail();
    this.role = user.getRole();
  }
}
