package com.sparta.deliveryapp.user.controller;


import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignInResponseDto;
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

    checkValidationErrors(bindingResult);

    userService.signUp(requestDto);

    return ResponseEntity.ok("Message: 회원가입이 완료되었습니다.");
  }

  @PostMapping("/sign-in")
  public ResponseEntity<SignInResponseDto> singIn(@Valid @RequestBody SignInRequestDto requestDto, BindingResult bindingResult) {

    checkValidationErrors(bindingResult);

    String token = userService.signIn(requestDto);

    // 응답 메시지와 JWT 토큰을 담은 DTO 생성
    SignInResponseDto response = new SignInResponseDto("로그인 성공", token);

    // 응답 반환
    return ResponseEntity.ok()
        .header("Authorization",token)
        .body(response);
  }



  // 공통 유효성 검사 메서드
  private void checkValidationErrors(BindingResult bindingResult) {
    // 유효성 검사 실패 시
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }

      // 400 Bad Request와 함께 오류 메시지 반환
      ResponseEntity.badRequest().body("error: " + errors.toString());
    }
  }
}
