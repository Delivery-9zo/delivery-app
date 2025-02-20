package com.sparta.deliveryapp.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
  private String userName;
  private String nickName;
  private String password;
  private String userAddress;

}
