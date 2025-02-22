package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
  @GetMapping("/location")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<StoreNearbyStoreResponseDto>> getNearbyStoresWithoutCategory(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @PageableDefault(
          size = 10,
          page = 0,
          sort = "distanceFromRequest",
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreNearbyStoreResponseDto> storeResponseDto = storeService.findNearbyStoresWithoutCategory(
        longitude, latitude, pageable);

    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDto);
  }


}