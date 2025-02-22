package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/stores")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
public class StoreMasterController {

  private final StoreService storeService;

  @DeleteMapping("/{storeId}")
  public ResponseEntity<String> deleteStore(
      @PathVariable String storeId) {

    Store deletedStore = storeService.deleteStore(storeId);

    return ResponseEntity.ok().body(deletedStore.getDeletedAt()+" 가게가 정상적으로 삭제되었습니다.");
  }

  //이름으로 가게 목록 조회
  @GetMapping("")
  public ResponseEntity<Page<StoreResponseDto>> findStoresByStoreName(
      @RequestParam(value = "storeName") String storeName,
      @PageableDefault(
          size = 10,
          page = 0,
          sort = "storeName",
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreResponseDto> storeResponseDtos = storeService.findStoresByStoreName(storeName, pageable);

    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDtos);
  }

  //가게 id로 가게 조회
  @GetMapping("/{storeId}")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<StoreResponseDto> findStoresByStoreId(
      @PathVariable String storeId) {

    StoreResponseDto storeResponseDto = storeService.findStoresByStoreId(storeId);

    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    return ResponseEntity.ok().body(storeResponseDto);
  }



}
