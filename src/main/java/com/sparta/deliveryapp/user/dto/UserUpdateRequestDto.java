package com.sparta.deliveryapp.user.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequestDto {
  private String userName;
  private String nickName;
  private String password;
  private String address;

}
