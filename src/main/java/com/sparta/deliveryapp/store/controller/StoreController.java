package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

  private final StoreService storeService;

  @PostMapping("/regi")
  public ResponseEntity<String> regiStore(
      @Valid @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.regiStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
  }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(
        @PathVariable String storeId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //로그인한 사용자가 유저 테이블의 role:master가 맞으면
        storeService.deleteStore(storeId, userDetails);

        //Todo: 반환에 삭제된 가게 상호명 및 삭제 시간 추가하여 반환하기
        return ResponseEntity.ok().body("가게가 정상적으로 삭제되었습니다.");
    }
}