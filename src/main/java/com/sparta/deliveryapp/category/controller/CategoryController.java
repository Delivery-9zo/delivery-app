package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@PreAuthorize("isAuthenticated()")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping("")
  public ResponseEntity<Page<CategoryResponseDto>> getAllCategories(
      @PageableDefault(
          size = 10,
          page = 0,
          sort = "createdAt",
          direction = Direction.ASC) Pageable pageable
  ) {

    Page<CategoryResponseDto> categoryList = categoryService.getAllCategories(pageable);

    return ResponseEntity.ok().body(categoryList);
  }
}
