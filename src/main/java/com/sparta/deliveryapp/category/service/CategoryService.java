package com.sparta.deliveryapp.category.service;

import static com.sparta.deliveryapp.commons.exception.ErrorCode.ALREADY_REGISTERED_CATEGORY;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.NOT_EXISTS_CATEGORY;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.NOT_EXISTS_INPUT_CATEGORY_DATA;
import static com.sparta.deliveryapp.commons.exception.ErrorCode.REGISTERED_FAILED_CATEGORY;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public void regiCategory(String categoryName) {

    if (categoryRepository.existsByCategoryName(categoryName)) {
      throw new CustomException(ALREADY_REGISTERED_CATEGORY);
    }
    Category category = new Category();
    category.setCategoryName(categoryName);

    try {
      categoryRepository.save(category);
    } catch (Exception e) {
      throw new CustomException(REGISTERED_FAILED_CATEGORY);
    }

  }

  public void updateCategoryByName(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    String categoryName = categoryUpdateRequestDto.getCategoryName() == null ? ""
        : categoryUpdateRequestDto.getCategoryName();
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? ""
        : categoryUpdateRequestDto.getNewCategoryName();

    if (categoryName.isEmpty() || newCategoryName.isEmpty()) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryName(categoryName);

    if (categoryOptional.isEmpty()) {
      throw new CustomException(NOT_EXISTS_CATEGORY);
    }

    Category category = categoryOptional.get();

    category.setCategoryName(categoryUpdateRequestDto.getNewCategoryName());

    try {
      categoryRepository.save(category);  // 카테고리 저장
    } catch (Exception e) {
      throw new CustomException(REGISTERED_FAILED_CATEGORY);
    }
  }

  public void updateCategoryById(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    UUID categoryId = categoryUpdateRequestDto.getCategoryId();
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? ""
        : categoryUpdateRequestDto.getNewCategoryName();

    if (categoryId == null) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }
    if (newCategoryName.trim().isEmpty()) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryId(categoryId);

    if (categoryOptional.isEmpty()) {
      throw new CustomException(NOT_EXISTS_CATEGORY);
    }

    Category category = categoryOptional.get();

    category.setCategoryName(categoryUpdateRequestDto.getNewCategoryName());

    try {
      categoryRepository.save(category);  // 카테고리 저장
    } catch (Exception e) {
      throw new CustomException(REGISTERED_FAILED_CATEGORY);
    }
  }


  public void deleteCategoryByName(CategoryRequestDto categoryRequestDto) {

    String categoryName =
        categoryRequestDto.getCategoryName() == null ? "" : categoryRequestDto.getCategoryName();

    if (categoryName.isEmpty()) {
      throw new IllegalArgumentException("카테고리가 입력되지 않았습니다.");
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryName(categoryName);

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
    }

    try {
      categoryRepository.delete(categoryOptional.get());
    } catch (Exception e) {
      throw new RuntimeException("카테고리 삭제 실패");
    }

  }

  public void deleteCategoryById(CategoryRequestDto categoryRequestDto) {
    UUID categoryId = categoryRequestDto.getCategoryId();

    if (categoryId == null) {
      throw new IllegalArgumentException("카테고리가 입력되지 않았습니다.");
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryId(categoryId);

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
    }

    try {
      categoryRepository.delete(categoryOptional.get());
    } catch (Exception e) {
      throw new RuntimeException("카테고리 삭제 실패");
    }
  }

  public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {

    Page<CategoryResponseDto> categoryList = categoryRepository.findAll(pageable)
        .map(category -> CategoryResponseDto.builder()
            .categoryId(category.getCategoryId())
            .categoryName(category.getCategoryName())
            .createAt(category.getCreatedAt())
            .build());

    if (categoryList.isEmpty()) {
      throw new NoSuchElementException("등록된 카테고리가 없습니다.");
    }

    return categoryList;
  }

  public CategoryResponseDto getCategoryById(UUID categoryId) {

    if (categoryId == null) {
      throw new IllegalArgumentException("카테고리 id가 없습니다.");
    }

    Optional<Category> category = categoryRepository.findByCategoryId(categoryId);

    if (category.isEmpty()) {
      throw new NoSuchElementException("카테고리 id에 매칭되는 카테고리가 없습니다.");
    }

    CategoryResponseDto categoryResponseDto = category.map(o ->
        CategoryResponseDto.builder()
            .categoryId(o.getCategoryId())
            .categoryName(o.getCategoryName())
            .createAt(o.getCreatedAt())
            .build()
    ).orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

    return categoryResponseDto;

  }


}
