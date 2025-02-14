package com.sparta.deliveryapp.user.controller;


import com.sparta.deliveryapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
}
