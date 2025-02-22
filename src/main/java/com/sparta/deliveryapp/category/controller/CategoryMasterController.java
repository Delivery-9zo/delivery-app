package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    return ResponseEntity.ok().body("카테고리 : " + categoryRequest.getCategoryName() + "이(가) 정상적으로 등록되었습니다.");
  }

  //카테고리 이름으로 변경
  @PutMapping("/name")
  public ResponseEntity<String> updateCategoryUsingName(@RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryUsingName(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 id로 변경
  @PutMapping("/id")
  public ResponseEntity<String> updateCategoryUsingId(@RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryUsingId(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }
}
