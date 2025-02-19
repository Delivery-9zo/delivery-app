package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

  private final StoreService storeService;

  @PostMapping("/regi")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<String> regiStore(
      @Valid @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.regiStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
  }




}