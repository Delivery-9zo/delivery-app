package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/regi")
  @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
  public ResponseEntity<String> regiCategory(@RequestBody CategoryRequestDto categoryRequest) {

    categoryService.regiCategory(categoryRequest.getCategoryName());

    return ResponseEntity.ok().body("카테고리 : " + categoryRequest.getCategoryName() + "이(가) 정상적으로 등록되었습니다.");
  }

  
}
