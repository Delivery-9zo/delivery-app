package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.entity.StoreCategory;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

  // 특정 Store와 연관된 StoreCategory 삭제
  @Modifying
  @Transactional
  @Query("UPDATE StoreCategory sc SET sc.deletedAt = CURRENT_TIMESTAMP, sc.deletedBy = :deletedBy WHERE sc.store.storeId = :storeId")
  void deleteStoreCategories(@Param("deletedBy") String deletedBy,
      @Param("storeId") UUID storeId);

  @Modifying
  @Transactional
  @Query("UPDATE StoreCategory sc SET sc.deletedAt = CURRENT_TIMESTAMP, sc.deletedBy = :deletedBy WHERE sc.category.categoryId = :categoryId")
  void deleteStoreCategoriesByCategoryId(@Param("deletedBy") String deletedBy,
      @Param("categoryId") UUID categoryId);

  @Modifying
  @Transactional
  @Query("UPDATE Store s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.storeId = :storeId")
  List<StoreCategory> findAllByCategoryId(@NotBlank String categoryName);
}

