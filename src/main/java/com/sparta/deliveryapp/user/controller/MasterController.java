package com.sparta.deliveryapp.user.controller;

import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "마스터유저 API", description = "마스터 권한을 가진 사람이 사용가능한 API")
@RequestMapping("/api/master")
public class MasterController {
  private final UserService userService;

  // 마스터 접근자 권한
  @GetMapping("/users")
  @Operation(summary = "유저조회 기능", description = "전체 유저를 조회하는 api")
  @PreAuthorize("hasAuthority('ROLE_MASTER')")
  public List<UserResponseDto> getUsers(){
    return userService.getUsers();
  }
}
