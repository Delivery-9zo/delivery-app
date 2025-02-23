package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.category.entity.Category;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

  // 특정 Store와 연관된 StoreCategory 삭제
  void deleteByStore(Store store);

  // 특정 Store와 Category의 관계 삭제
  void deleteByStoreAndCategory(Store store, Category category);
}

