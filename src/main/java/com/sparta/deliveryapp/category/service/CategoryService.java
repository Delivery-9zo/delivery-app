package com.sparta.deliveryapp.category.service;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryResponseDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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

  //todo: throws로 구현할지 생각해보기
  @Transactional
  public void regiCategory(String categoryName) {

    if (categoryRepository.existsByCategoryName(categoryName)) {
      throw new IllegalArgumentException("동일한 카테고리가 존재합니다.");
    }
    Category category = new Category();
    category.setCategoryName(categoryName);

    try {
      categoryRepository.save(category);
    } catch (Exception e) {
      throw new IllegalArgumentException("카테고리 등록이 실패했습니다.");
    }

  }

  public void updateCategoryByName(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    String categoryName = categoryUpdateRequestDto.getCategoryName() == null ? ""
        : categoryUpdateRequestDto.getCategoryName();
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? ""
        : categoryUpdateRequestDto.getNewCategoryName();

    if (categoryName.isEmpty() || newCategoryName.isEmpty()) {
      throw new IllegalArgumentException("카테고리나 새로운 카테고리가 입력되지 않았습니다.");
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryName(categoryName);

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
    }

    Category category = categoryOptional.get();

    category.setCategoryName(categoryUpdateRequestDto.getNewCategoryName());

    try {
      categoryRepository.save(category);  // 카테고리 저장
    } catch (Exception e) {
      throw new RuntimeException("카테고리 업데이트에 실패했습니다.");
    }
  }

  public void updateCategoryById(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    UUID categoryId = categoryUpdateRequestDto.getCategoryId();
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? ""
        : categoryUpdateRequestDto.getNewCategoryName();

    if (categoryId == null) {
      throw new IllegalArgumentException("카테고리 id가 비어있습니다.");
    }
    if (newCategoryName.trim().isEmpty()) {
      throw new IllegalArgumentException("카테고리 명이 없습니다.");
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryId(categoryId);

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 카테고리 id입니다.");
    }

    Category category = categoryOptional.get();

    category.setCategoryName(categoryUpdateRequestDto.getNewCategoryName());

    try {
      categoryRepository.save(category);  // 카테고리 저장
    } catch (Exception e) {
      throw new RuntimeException("카테고리 업데이트에 실패했습니다.");
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
            .build());


    if (categoryList.isEmpty()) {
      throw new NoSuchElementException("등록된 카테고리가 없습니다.");
    }

    return categoryList;
  }
}
