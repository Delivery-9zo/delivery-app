package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "카테고리 조회 기능 API", description = "카테고리 조회 기능을 제공하는 API")
public class CategoryController {

  private final CategoryService categoryService;

  @Operation(
      summary = "카테고리 전체 조회 기능",
      description = "등록된 모든 카테고리를 제공하는 API",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리 목록 조회 성공",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CategoryResponseDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "서버 에러",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "{\n" +
                          "  \"message\": \"서버 에러가 발생했습니다.\"\n" +
                          "}"
                  )
              )
          )
      }
  )
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
