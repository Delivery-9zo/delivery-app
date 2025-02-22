package com.sparta.deliveryapp.category.service;

import com.sparta.deliveryapp.category.dto.CategoryRequestDto;
import com.sparta.deliveryapp.category.dto.CategoryUpdateRequestDto;
import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  //todo: throws로 구현할지 생각해보기
  @Transactional
  public void regiCategory(String categoryName) {

    if(categoryRepository.existsByCategoryName(categoryName)){
      throw new IllegalArgumentException("동일한 카테고리가 존재합니다.");
    }
    Category category = new Category();
    category.setCategoryName(categoryName);

    try{
      categoryRepository.save(category);
    }
    catch (Exception e){
      log.error(e.getMessage()+"카테고리 등록 실패");
      throw new IllegalArgumentException("카테고리 등록이 실패했습니다.");
    }

  }

  public void updateCategoryUsingName(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    String categoryName = categoryUpdateRequestDto.getCategoryName() == null ? "" : categoryUpdateRequestDto.getCategoryName();
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? "" : categoryUpdateRequestDto.getNewCategoryName();

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
      log.error("카테고리 업데이트 실패: " + e.getMessage());
      throw new RuntimeException("카테고리 업데이트에 실패했습니다.");
    }
  }


  public void updateCategoryUsingId(CategoryUpdateRequestDto categoryUpdateRequestDto) {

    String categoryId = categoryUpdateRequestDto.getCategoryId() == null? "" : String.valueOf(categoryUpdateRequestDto.getCategoryId());
    String newCategoryName = categoryUpdateRequestDto.getNewCategoryName() == null ? "" : categoryUpdateRequestDto.getNewCategoryName();

    if (categoryId.trim().isEmpty()) {
      throw new IllegalArgumentException("카테고리 id가 비어있습니다.");
    }
    if(newCategoryName.trim().isEmpty()){
      throw new IllegalArgumentException("카테고리 명이 없습니다.");
    }

    Optional<Category> categoryOptional = categoryRepository.findByCategoryId(UUID.fromString(categoryId));

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 카테고리 id입니다.");
    }

    Category category = categoryOptional.get();

    category.setCategoryName(categoryUpdateRequestDto.getNewCategoryName());

    try {
      categoryRepository.save(category);  // 카테고리 저장
    } catch (Exception e) {
      log.error("카테고리 업데이트 실패: " + e.getMessage());
      throw new RuntimeException("카테고리 업데이트에 실패했습니다.");
    }
  }


}
