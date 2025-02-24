package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/categories")
@PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
@Tag(name = "카테고리 기능 API", description = "카테고리 정보 관리 기능을 제공하는 API")
public class CategoryMasterController {

  private final CategoryService categoryService;

  @PostMapping("/regi")
  @Operation(summary = "카테고리 등록 기능", description = "카테고리를 등록하는 API")
  public ResponseEntity<String> regiCategory(@RequestBody CategoryRequestDto categoryRequest) {

    categoryService.regiCategory(categoryRequest.getCategoryName());

    return ResponseEntity.ok()
        .body("카테고리 : " + categoryRequest.getCategoryName() + "이(가) 정상적으로 등록되었습니다.");
  }

  //카테고리 이름으로 변경
  @PutMapping("/name")
  @Operation(summary = "카테고리 수정 기능(이름 기준)", description = "등록된 카테고리 이름으로 카테고리 정보를 수정하는 API")
  public ResponseEntity<String> updateCategoryByName(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryByName(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 id로 변경
  @PutMapping("/id")
  @Operation(summary = "카테고리 수정 기능(ID 기준)", description = "등록된 카테고리 UUID로 카테고리 정보를 수정하는 API")
  public ResponseEntity<String> updateCategoryById(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryById(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 이름으로 삭제
  @DeleteMapping("/name")
  @Operation(summary = "카테고리 삭제 기능(이름 기준)", description = "등록된 카테고리 이름으로 검색하여 삭제하는 API")
  public ResponseEntity<String> deleteCategoryByName(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryByName(categoryRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }

  //카테고리 id로 삭제
  @DeleteMapping("/id")
  @Operation(summary = "카테고리 삭제 기능(ID 기준)", description = "등록된 카테고리 UUID로 검색하여 삭제하는 API")
  public ResponseEntity<String> deleteCategoryById(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryById(categoryRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }

  @GetMapping("")
  @Operation(summary = "카테고리 검색 기능(이름 기준)", description = "등록된 카테고리 이름으로 검색하여 정보를 조회하는 API")
  public ResponseEntity<CategoryResponseDto> getCategoryByName(@RequestParam(name = "id") UUID categoryId) {

    CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(categoryId);

    return ResponseEntity.ok().body(categoryResponseDto);
  }
}
