package com.sparta.deliveryapp.category.repository;

import com.sparta.deliveryapp.category.entity.Category;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

  boolean existsByCategoryName(@NotBlank String categoryName);

  Optional<Category> findByCategoryId(UUID categoryId);

  Optional<Category> findByCategoryName(@NotBlank String categoryName);

  Page<Category> findAll(Pageable pageable);

  List<Category> findByCategoryNameIn(List<String> categories);

  @Modifying
  @Transactional
  @Query("UPDATE Category c SET c.deletedAt = CURRENT_TIMESTAMP, c.deletedBy = :deletedBy WHERE c.categoryId = :categoryId")
  void deleteCategory(@Param("categoryId") UUID categoryId, @Param("deletedBy") String deletedBy);


}
