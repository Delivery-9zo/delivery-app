package com.sparta.deliveryapp.menu.controller;

import com.sparta.deliveryapp.menu.dto.MenuGetResponseDto;
import com.sparta.deliveryapp.menu.dto.MenuPatchRequestDto;
import com.sparta.deliveryapp.menu.dto.MenuPostRequestDto;
import com.sparta.deliveryapp.menu.service.MenuService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  @PostMapping
  public ResponseEntity<String> postMenu(
      @RequestBody MenuPostRequestDto dto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.postMenu(dto, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("Message: " + "메뉴가 등록되었습니다.");
  }

  @GetMapping("/menu/{storeId}")
  public ResponseEntity<MenuGetResponseDto> getMenu(
      @RequestParam(name = "menuName") String menuName,
      @PathVariable(name = "storeId") UUID storeId
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(menuService.getMenu(menuName, storeId));
  }

  @GetMapping("/{storeId}")
  public ResponseEntity<Page<MenuGetResponseDto>> getMenus(
      @PathVariable(name = "storeId") UUID storeId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(menuService.getMenus(storeId, pageable));
  }

  @PatchMapping("/{storeId}")
  public ResponseEntity<String> patchMenu(
      @PathVariable(name = "storeId") UUID storeId,
      @RequestParam(name = "menuName") String menuName,
      @RequestBody MenuPatchRequestDto dto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.patchMenu(menuName, storeId, dto, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("Message: " + "메뉴가 수정되었습니다.");
  }

  @DeleteMapping
  public ResponseEntity<String> deleteMenu(
      @RequestParam(name = "menuId") UUID menuId,
      @RequestParam(name = "storeId") UUID storeId,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.deleteMenu(menuId, storeId, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("Message: " + "메뉴가 삭제되었습니다.");
  }

}
