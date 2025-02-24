package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
      @SortDefault.SortDefaults({
          @SortDefault(sort = "createAt", direction = Direction.ASC),
          @SortDefault(sort = "distanceFromRequest", direction = Direction.ASC)
      })
      @PageableDefault(size = 10, page = 0) Pageable pageable) {

    Page<StoreNearbyStoreResponseDto> storeResponseDto = storeService.findNearbyStoresWithoutCategory(
        longitude, latitude, pageable);

    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDto);
  }

  //카테고리에 해당하는 주변 가게 검색
  @GetMapping("/locandcat")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<StoreNearbyStoreWithCategoryResponseDto>> getNearbyStoresWithCategory(
      @RequestParam(value = "category") List<String> categoryNames,
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @SortDefault.SortDefaults({
          @SortDefault(sort = "createAt", direction = Direction.ASC),
          @SortDefault(sort = "distanceFromRequest", direction = Direction.ASC)
      })
      @PageableDefault(
          size = 10,
          page = 0) Pageable pageable) {

    Page<StoreNearbyStoreWithCategoryResponseDto> storeDtos = storeService.findNearbyStoresByCategory(
        categoryNames, longitude, latitude, pageable);

    return ResponseEntity.ok().body(storeDtos);
  }

  @PutMapping("")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<String> updateStore(
      @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.updateStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게 정보가 정상적으로 수정되었습니다.");
  }
}