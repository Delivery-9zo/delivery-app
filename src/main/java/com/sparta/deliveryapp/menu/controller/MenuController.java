package com.sparta.deliveryapp.menu.controller;

import com.sparta.deliveryapp.menu.dto.MenuGetResponseDto;
import com.sparta.deliveryapp.menu.dto.MenuPatchRequestDto;
import com.sparta.deliveryapp.menu.dto.MenuPostRequestDto;
import com.sparta.deliveryapp.menu.dto.MenuSwaggerExample;
import com.sparta.deliveryapp.menu.service.MenuService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "메뉴 API", description = "메뉴 CRUD 관련 API 엔드포인트")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;


  @Operation(summary = "메뉴 추가", description = "메뉴 정보를 전달하여 메뉴를 추가합니다.")
  @ApiResponse(responseCode = "200", description = "메뉴 추가 성공", content = @Content(
      schema = @Schema(type = "string", example = "메뉴가 등록되었습니다."),
      mediaType = "application/json"
  ))
  @PostMapping
  public ResponseEntity<String> postMenu(
      @RequestBody
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MenuPostRequestDto.class)
          )
      )
      MenuPostRequestDto dto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.postMenu(dto, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("메뉴가 등록되었습니다.");
  }

  @Operation(summary = "이름으로 메뉴 조회", description = "메뉴 이름으로 가게의 메뉴를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "메뉴 조회 성공",
      content = @Content(schema = @Schema(implementation = MenuGetResponseDto.class),
          mediaType = "application/json"))
  @Parameter(name = "menuName", description = "조회할 메뉴의 이름", required = true, example = "수박")
  @Parameter(name = "storeId", description = "메뉴 조회할 가게 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
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

  @Operation(summary = "가게의 모든 메뉴 조회", description = "가게의 모든 메뉴를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "메뉴 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "페이징 처리된 메뉴 목록",
          value = MenuSwaggerExample.MENU_LIST_EXAMPLE
      )))
  @Parameter(name = "storeId", description = "메뉴 조회할 가게 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
  @Parameter(name = "size", description = "페이지 크기", example = "10")
  @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
  @Parameter(name = "sort", description = "정렬 기준 필드 (예: createdAt,asc 또는 createdAt,desc)",
      example = "createdAt,desc")
  @GetMapping("/{storeId}")
  public ResponseEntity<Page<MenuGetResponseDto>> getMenus(
      @PathVariable(name = "storeId") UUID storeId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
      @Parameter(hidden = true) Pageable pageable
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(menuService.getMenus(storeId, pageable));
  }

  @Operation(summary = "메뉴 정보 수정", description = "기존 메뉴를 수정합니다.")
  @ApiResponse(responseCode = "200", description = "메뉴 수정 성공",
      content = @Content(schema = @Schema(type = "string", example = "메뉴가 수정되었습니다.")))
  @Parameter(name = "storeId", description = "메뉴 조회할 가게 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
  @Parameter(name = "menuName", description = "조회할 메뉴의 이름", required = true, example = "수박")
  @PatchMapping("/{storeId}")
  public ResponseEntity<String> patchMenu(
      @PathVariable(name = "storeId") UUID storeId,
      @RequestParam(name = "menuName") String menuName,
      @RequestBody
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MenuPatchRequestDto.class)
          )
      )
      MenuPatchRequestDto dto,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.patchMenu(menuName, storeId, dto, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("메뉴가 수정되었습니다.");
  }

  @Operation(summary = "메뉴 삭제", description = "기존 메뉴를 삭제합니다.")
  @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공",
      content = @Content(schema = @Schema(type = "string", example = "메뉴가 삭제되었습니다.")))
  @Parameter(name = "storeId", description = "메뉴 삭제할 가게 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
  @Parameter(name = "menuName", description = "삭제할 메뉴의 이름", required = true, example = "수박")
  @DeleteMapping
  public ResponseEntity<String> deleteMenu(
      @RequestParam(name = "menuId") UUID menuId,
      @RequestParam(name = "storeId") UUID storeId,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    menuService.deleteMenuById(menuId, storeId, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("메뉴가 삭제되었습니다.");
  }

}
