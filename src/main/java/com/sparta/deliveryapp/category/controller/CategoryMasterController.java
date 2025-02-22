package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/categories")
@PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
public class CategoryMasterController {

  private final CategoryService categoryService;

  @PostMapping("/regi")
  public ResponseEntity<String> regiCategory(@RequestBody CategoryRequestDto categoryRequest) {

    categoryService.regiCategory(categoryRequest.getCategoryName());

    return ResponseEntity.ok()
        .body("카테고리 : " + categoryRequest.getCategoryName() + "이(가) 정상적으로 등록되었습니다.");
  }

  //카테고리 이름으로 변경
  @PutMapping("/name")
  public ResponseEntity<String> updateCategoryByName(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryByName(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 id로 변경
  @PutMapping("/id")
  public ResponseEntity<String> updateCategoryById(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryById(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 이름으로 삭제
  @DeleteMapping("/name")
  public ResponseEntity<String> deleteCategoryByName(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryByName(categoryRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }

  //카테고리 id로 삭제
  @DeleteMapping("/id")
  public ResponseEntity<String> deleteCategoryById(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryById(categoryRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }

  @GetMapping("")
  public ResponseEntity<CategoryResponseDto> getCategoryByName(@RequestParam(name = "id") UUID categoryId) {

    CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(categoryId);

    return ResponseEntity.ok().body(categoryResponseDto);
  }
}
