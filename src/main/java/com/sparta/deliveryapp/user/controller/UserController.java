package com.sparta.deliveryapp.user.controller;


import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
public class UserController {

  private final UserService userService;

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto requestDto,
      BindingResult bindingResult) {

    ResponseEntity<String> errorResponse = checkValidationErrors(bindingResult);
    if (errorResponse != null) {
      return errorResponse;
    }

    userService.signUp(requestDto);

    return ResponseEntity.ok("Message: 회원가입이 완료되었습니다.");
  }

  @PostMapping("/sign-in")
  public ResponseEntity<String> singIn(@Valid @RequestBody SignInRequestDto requestDto, BindingResult bindingResult) {

    ResponseEntity<String> errorResponse = checkValidationErrors(bindingResult);
    if (errorResponse != null) {
      return errorResponse;
    }
    userService.signIn(requestDto);

    // 추후 Jwt 반환 할 예정
    return ResponseEntity.ok("로그인 성공");
  }



  // 공통 유효성 검사 메서드
  private ResponseEntity<String> checkValidationErrors(BindingResult bindingResult) {
    // 유효성 검사 실패 시
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }

      // 400 Bad Request와 함께 오류 메시지 반환
      return ResponseEntity.badRequest().body("error: " + errors.toString());
    }
    return null;  // 유효성 검사 통과
  }
}
