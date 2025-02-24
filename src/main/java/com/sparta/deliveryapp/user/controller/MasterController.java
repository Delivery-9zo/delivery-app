package com.sparta.deliveryapp.user.controller;

import com.sparta.deliveryapp.user.dto.MasterUserUpdateRequestDto;
import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import com.sparta.deliveryapp.user.service.MasterUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "마스터유저 API", description = "마스터 권한을 가진 사람이 사용가능한 API")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
@RequestMapping("/api/master/users")
public class MasterController {
  private final MasterUserService masterUserService;

  @GetMapping("/users")
  @Operation(summary = "유저조회 기능", description = "전체 유저를 조회하는 api")
  public List<UserResponseDto> getUsers(){
    return masterUserService.getUsers();
  }

  @PutMapping("/{email}")
  @Operation(summary = "유저정보 수정 기능",description = "하나의 유저의 정보를 변경")
  public UserResponseDto updateUser(@PathVariable String email, @RequestBody MasterUserUpdateRequestDto requestDto, @AuthenticationPrincipal
  UserDetailsImpl userDetails){
    return masterUserService.updateUser(email,requestDto);
  }

}
