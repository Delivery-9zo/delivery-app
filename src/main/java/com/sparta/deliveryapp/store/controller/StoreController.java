package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

  //카테고리 없이 경위도로만 근처 가게 검색
  @GetMapping("location/{longitude}/{latitude}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<StoreNearbyStoreResponseDto>> getNearbyStoresWithoutCategory(
      @PathVariable double longitude,
      @PathVariable double latitude,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<StoreNearbyStoreResponseDto> storeResponseDto = storeService.findNearbyStoresWithoutCategory(longitude, latitude, userDetails);

    log.info(storeResponseDto.toString());
    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDto);
  }


}