package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/stores")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
public class StoreMasterController {

  private final StoreService storeService;

  @DeleteMapping("/{storeId}")
  public ResponseEntity<String> deleteStore(
      @PathVariable String storeId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    Store deletedStore = storeService.deleteStore(storeId, userDetails);

    return ResponseEntity.ok().body(deletedStore.getDeletedAt()+" 가게가 정상적으로 삭제되었습니다.");
  }

  //이름으로 가게 목록 조회
  @GetMapping("")
  public ResponseEntity<List<StoreResponseDto>> findStoresByStoreName(
      @RequestParam(value = "storeName") String storeName,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    log.info("findStoresByStoreName 호출 storename: "+storeName);
    List<StoreResponseDto> storeResponseDtos = storeService.findStoresByStoreName(storeName, userDetails);
    //todo: 커스텀 AccessDenied 예외 처리 추가(GlobalExceptionHandler에)
    log.info(storeResponseDtos.toString());
    return ResponseEntity.ok().body(storeResponseDtos);
  }

}
