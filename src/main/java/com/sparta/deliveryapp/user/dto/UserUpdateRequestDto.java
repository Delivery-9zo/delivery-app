package com.sparta.deliveryapp.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

  @Schema(description = "유저 이름",example = "수정한 이름")
  private String userName;

  @Schema(description = "유저 닉네임",example = "수정한 이메일")
  private String nickName;

  @Schema(description = "유저 비밀번호",example = "1234")
  private String password;

  @Schema(description = "유저 주소",example = "경기도 수풍로 90")
  private String userAddress;

}
