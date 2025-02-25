package com.sparta.deliveryapp.category.controller;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.service.CategoryService;
import com.sparta.deliveryapp.store.entity.StoreCategory;
import com.sparta.deliveryapp.store.repository.StoreCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/categories")
@PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
@Tag(name = "카테고리 기능 API", description = "카테고리 정보 관리 기능을 제공하는 API")
public class CategoryMasterController {

  private final CategoryService categoryService;
  private final StoreCategoryRepository storeCategoryRepository;

  @Operation(
      summary = "카테고리 등록 기능",
      description = "카테고리를 등록하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "카테고리 이름을 받아 등록하는 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryRequestDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리가 정상적으로 등록됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(type = "string")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/regi")
  public ResponseEntity<String> regiCategory(@RequestBody CategoryRequestDto categoryRequest) {

    categoryService.regiCategory(categoryRequest.getCategoryName());

    return ResponseEntity.ok()
        .body("카테고리 : " + categoryRequest.getCategoryName() + "이(가) 정상적으로 등록되었습니다.");
  }

  //카테고리 이름으로 변경
  @Operation(
      summary = "카테고리 수정 기능(이름 기준)",
      description = "등록된 카테고리 이름으로 카테고리 정보를 수정하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "수정할 카테고리 이름과 새 카테고리 이름을 받아 수정하는 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryUpdateRequestDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리가 정상적으로 수정됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(type = "string")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PutMapping("/name")
  public ResponseEntity<String> updateCategoryByName(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryByName(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 id로 변경
  @Operation(
      summary = "카테고리 수정 기능(ID 기준)",
      description = "등록된 카테고리 UUID로 카테고리 정보를 수정하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "수정할 카테고리 ID와 새 카테고리 이름을 받아 수정하는 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryUpdateRequestDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리가 정상적으로 수정됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(type = "string")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PutMapping("/id")
  public ResponseEntity<String> updateCategoryById(
      @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {

    categoryService.updateCategoryById(categoryUpdateRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 수정되었습니다.");
  }

  //카테고리 이름으로 삭제
  @Operation(
      summary = "카테고리 삭제 기능(이름 기준)",
      description = "등록된 카테고리 이름으로 검색하여 삭제하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "삭제할 카테고리 이름을 받아 삭제하는 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryRequestDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리가 정상적으로 삭제됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(type = "string")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @DeleteMapping("/name")
  public ResponseEntity<String> deleteCategoryByName(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryByName(categoryRequestDto);

    List<StoreCategory> storeCategories = storeCategoryRepository.findAllByCategoryId(categoryRequestDto.getCategoryName());

    storeCategories.forEach(storeCategory -> {
      storeCategoryRepository.deleteAllStoreCategoriesByCategoryName(storeCategories);
    });

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }

  //카테고리 id로 삭제
  @Operation(
      summary = "카테고리 삭제 기능(ID 기준)",
      description = "등록된 카테고리 UUID로 검색하여 삭제하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "삭제할 카테고리 ID를 받아 삭제하는 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryRequestDto.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리가 정상적으로 삭제됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(type = "string")
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @DeleteMapping("/id")
  public ResponseEntity<String> deleteCategoryById(
      @RequestBody CategoryRequestDto categoryRequestDto) {

    categoryService.deleteCategoryById(categoryRequestDto);

    return ResponseEntity.ok().body("카테고리가 정상적으로 삭제되었습니다.");
  }


  @Operation(
      summary = "카테고리 검색 기능(이름 기준)",
      description = "등록된 카테고리 이름으로 검색하여 정보를 조회하는 API",
      parameters = {
          @Parameter(name = "category_name", description = "조회할 카테고리 이름", required = true, example = "치킨")
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "카테고리 정보가 정상적으로 조회됨",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CategoryResponseDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "404",
              description = "카테고리를 찾을 수 없음",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @GetMapping("")
  public ResponseEntity<CategoryResponseDto> getCategoryByName(
      @RequestParam(name = "name") String categoryName) {

    CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(categoryName);

    return ResponseEntity.ok().body(categoryResponseDto);
  }
}
