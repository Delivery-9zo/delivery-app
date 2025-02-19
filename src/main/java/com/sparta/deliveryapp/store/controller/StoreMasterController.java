package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
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
  @GetMapping("/")
  public ResponseEntity<List<StoreResponseDto>> findStoresByName(
      @RequestParam(value = "name") String storeName,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<StoreResponseDto> storeResponseDtos = storeService.findStoresByName(storeName, userDetails);

    return ResponseEntity.ok().body(storeResponseDtos);
  }

}
