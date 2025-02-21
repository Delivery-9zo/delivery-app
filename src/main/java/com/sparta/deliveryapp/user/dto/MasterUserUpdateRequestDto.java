package com.sparta.deliveryapp.user.dto;


import com.sparta.deliveryapp.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MasterUserUpdateRequestDto {
  private String userName;
  private String nickName;
  private String userAddress;
  private UserRole role;

}
