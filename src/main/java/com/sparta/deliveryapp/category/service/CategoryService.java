package com.sparta.deliveryapp.category.service;

import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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
}
