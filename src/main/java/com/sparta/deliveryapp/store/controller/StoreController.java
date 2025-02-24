package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
@Tag(name = "상점 기능 API", description = "상점 등록, 검색, 정보 수정이 가능한 API")
public class StoreController {

  private final StoreService storeService;


  @PostMapping("/regi")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  @Operation(summary = "가게 등록 기능", description = "가게를 등록하는 API")
  public ResponseEntity<String> regiStore(
      @Valid @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.regiStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
  }

  //카테고리 없이 경위도로만 근처 가게 검색
  @GetMapping("/location")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "인근 가게 정보 검색(카테고리 미지정 시)", description = "인근 3km 이내의 모든 가게를 조회하는 API")
  public ResponseEntity<Page<StoreNearbyStoreResponseDto>> getNearbyStoresWithoutCategory(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @PageableDefault(size = 10, page = 0,
      sort = {"createdAt", "updatedAt", "distanceFromRequest"},
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreNearbyStoreResponseDto> storeResponseDto = storeService.findNearbyStoresWithoutCategory(
        longitude, latitude, pageable);

    return ResponseEntity.ok().body(storeResponseDto);
  }

  //카테고리에 해당하는 주변 가게 검색
  @GetMapping("/locandcat")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "인근 가게 정보 검색(카테고리 지정 시)", description = "인근 3km 이내의 일치하는 카테고리의 모든 가게를 조회하는 API")
  public ResponseEntity<Page<StoreNearbyStoreWithCategoryResponseDto>> getNearbyStoresWithCategory(
      @RequestParam(value = "category") List<String> categoryNames,
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @SortDefault.SortDefaults({
          @SortDefault(sort = "createdAt", direction = Direction.ASC),
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
  @Operation(summary = "가게 정보 수정(가게 주인 기능)", description = "가게 정보를 수정하는 API. 기존 정보와 변한 정보만 수정되어 저장. 요청한 유저와 가게 주인 정보가 일치해야 수정 가능.")
  public ResponseEntity<String> updateStore(
      @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.updateStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게 정보가 정상적으로 수정되었습니다.");
  }

  @GetMapping("/order")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  @Operation(summary = "가게의 주문 목록 조회", description = "가게의 주문 목록을 조회하는 API")
  public ResponseEntity<Page<SearchOrderResponseDto>> getStoreOrders(
      @RequestParam(name = "storeId") String storeId,
      @PageableDefault(size = 10, page = 0,
          sort = {"createdAt", "updatedAt"},
          direction = Direction.ASC) Pageable pageable ) {

    Page<SearchOrderResponseDto> orders = storeService.getOrdersByStore(storeId, pageable);

    return ResponseEntity.ok().body(orders);
  }
}