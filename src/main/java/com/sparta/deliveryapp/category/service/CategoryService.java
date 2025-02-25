package com.sparta.deliveryapp.category.service;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.store.repository.StoreCategoryRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.sparta.deliveryapp.commons.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final StoreCategoryRepository storeCategoryRepository;

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
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryName(categoryName);

    if (categoryOptional.isEmpty()) {
      throw new CustomException(NOT_EXISTS_CATEGORY);
    }

    try {
      categoryRepository.deleteCategory(categoryOptional.get().getCategoryId(),
          getCurrentUserEmail());
      storeCategoryRepository.deleteStoreCategoriesByCategoryId(getCurrentUserEmail(),
          categoryOptional.orElseThrow()
              .getCategoryId());
    } catch (Exception e) {
      throw new CustomException(UPDATED_FAILED_CATEGORY);
    }

  }

  public void deleteCategoryById(CategoryRequestDto categoryRequestDto) {
    UUID categoryId = categoryRequestDto.getCategoryId();

    if (categoryId == null) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryId(categoryId);

    if (categoryOptional.isEmpty()) {
      throw new CustomException(NOT_EXISTS_CATEGORY);
    }

    try {
      categoryRepository.deleteCategory(categoryOptional.get().getCategoryId(),
          getCurrentUserEmail());
      storeCategoryRepository.deleteStoreCategoriesByCategoryId(getCurrentUserEmail(), categoryId);
    } catch (Exception e) {
      throw new CustomException(UPDATED_FAILED_CATEGORY);
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
      throw new CustomException(NOT_EXISTS_CATEGORY);
    }

    return categoryList;
  }

  public CategoryResponseDto getCategoryById(String categoryId) {

    if (categoryId == null) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    Optional<Category> category = categoryRepository.findByCategoryName(categoryId);

    if (category.isEmpty()) {
      throw new CustomException(NOT_EXISTS_INPUT_CATEGORY_DATA);
    }

    CategoryResponseDto categoryResponseDto = category.map(o ->
        CategoryResponseDto.builder()
            .categoryId(o.getCategoryId())
            .categoryName(o.getCategoryName())
            .createAt(o.getCreatedAt())
            .build()
    ).orElseThrow(() -> new CustomException(NOT_EXISTS_CATEGORY));

    return categoryResponseDto;

  }

  private String getCurrentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
      return ((UserDetailsImpl) auth.getPrincipal()).getEmail();
    }
    throw new SecurityException("No authenticated user found");
  }
}
