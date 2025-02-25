package com.sparta.deliveryapp.user.controller;


import com.sparta.deliveryapp.user.dto.SignInRequestDto;
import com.sparta.deliveryapp.user.dto.SignInResponseDto;
import com.sparta.deliveryapp.user.dto.SignUpRequestDto;
import com.sparta.deliveryapp.user.dto.UserResponseDto;
import com.sparta.deliveryapp.user.dto.UserUpdateRequestDto;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import com.sparta.deliveryapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "유저 API", description = "유저 컨트롤러에 대한 설명입니다.")
@Slf4j
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원가입 기능", description = "회원가입 하는 api")
  @ApiResponse(responseCode = "200",description = "회원가입 성공")
  @PostMapping("/sign-up")
  public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignUpRequestDto requestDto,
      BindingResult bindingResult) {

    checkValidationErrors(bindingResult);

    userService.signUp(requestDto);

    return ResponseEntity.ok(Collections.singletonMap("message", "사용자가 성공적으로 생성되었습니다."));
  }

  @Operation(summary = "로그인 기능", description = "로그인 하는 api")
  @ApiResponse(responseCode = "200",description = "로그인 성공")
  @ApiResponse(responseCode = "403",description = "접근 권한이 없습니다.")
  @PostMapping("/sign-in")
  public ResponseEntity<SignInResponseDto> singIn(@Valid @RequestBody SignInRequestDto requestDto,
      BindingResult bindingResult) {

    checkValidationErrors(bindingResult);

    String token = userService.signIn(requestDto);

    // 응답 메시지와 JWT 토큰을 담은 DTO 생성
    SignInResponseDto response = new SignInResponseDto("로그인 성공", token);

    // 응답 반환
    return ResponseEntity.ok()
        .header("Authorization", token)
        .body(response);
  }

  @Operation(summary = "수정 기능", description = "유저정보를 수정하는 api, null 로 값을 보내주면 기존의 데이터가 유지되고 받은 값들만 변경이 가능합니다.")
  @ApiResponse(responseCode = "200",description = "유저정보 수정 성공")
  @PutMapping()
  public ResponseEntity<Map<String, String>> updateUser(
      @RequestBody UserUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    String email = userDetails.getUser().getEmail();
    userService.updateUser(email, requestDto, userDetails.getUser());

    return ResponseEntity.ok(Collections.singletonMap("message", "사용자 정보가 성공적으로 업데이트되었습니다."));
  }

  @Operation(summary = "삭제 기능", description = "유저정보를 삭제하는 api")
  @DeleteMapping()
  public ResponseEntity<Map<String, String>> deleteUser( @AuthenticationPrincipal UserDetailsImpl userDetails) {
    String email = userDetails.getUser().getEmail();
    userService.deleteUser(email, userDetails.getUser());

    return ResponseEntity.ok(Collections.singletonMap("message", "사용자 정보가 성공적으로 삭제되었습니다."));
  }

  @Operation(summary = "조회 기능", description = "개인정보를 조회하는 api")
  @GetMapping()
  public UserResponseDto getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    String email = userDetails.getUser().getEmail();
    return userService.getUser(email, userDetails.getUser());

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
