package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/stores")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
@Tag(name = "상점 기능 마스터유저 API", description = "마스터 권한을 가진 사람이 사용 가능한 API")
public class StoreMasterController {

  private final StoreService storeService;

  @DeleteMapping("/{storeId}")
  @Operation(summary = "가게 삭제 기능", description = "가게 UUID를 이용하여 가게를 삭제하는 API")
  public ResponseEntity<String> deleteStore(
      @PathVariable String storeId) {

    Store deletedStore = storeService.deleteStore(storeId);

    return ResponseEntity.ok().body(deletedStore.getDeletedAt()+" 가게가 정상적으로 삭제되었습니다.");
  }

  //이름으로 가게 목록 조회
  @GetMapping("")
  @Operation(summary = "가게 이름으로 검색", description = "가게 이름을 검색하여 일치하는 이름의 가게 목록을 조회하는 API. 기본 정렬 순서 생성 시간 오름차순.")
  public ResponseEntity<Page<StoreResponseDto>> findStoresByStoreName(
      @RequestParam(value = "storeName") String storeName,
      @PageableDefault(
          size = 10,
          page = 0,
          sort = "createAt",
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreResponseDto> storeResponseDtos = storeService.findStoresByStoreName(storeName, pageable);

    return ResponseEntity.ok().body(storeResponseDtos);
  }

  //가게 id로 가게 조회
  @GetMapping("/sid")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  @Operation(summary = "가게 UUID로 조회", description = "가게 UUID로 조회하여 가게 정보를 제공하는 API. 가게 주인과 서버 관리자 이용 가능.")
  public ResponseEntity<StoreResponseDto> findStoresByStoreId(
      @RequestParam(name = "sid") String storeId) {

    StoreResponseDto storeResponseDto = storeService.findStoresByStoreId(UUID.fromString(storeId));

    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDto);
  }

  @PutMapping("")
  @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
  @Operation(summary = "가게 정보 수정(마스터)", description = "가게 정보를 수정하는 API. 기존 정보와 변한 정보만 수정되어 저장.")
  public ResponseEntity<String> updateStore(
      @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.updateStoreMaster(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게 정보가 정상적으로 수정되었습니다.");
  }

}
