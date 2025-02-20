package com.sparta.deliveryapp.user.controller;

import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master")
public class MasterController {
  private final UserService userService;

  // 마스터 접근자 권한
  @GetMapping("/users")
  @PreAuthorize("hasAuthority('ROLE_MASTER')")
  public List<UserResponseDto> getUsers(){
    return userService.getUsers();
  }
}
