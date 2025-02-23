package com.sparta.deliveryapp.category.repository;

import com.sparta.deliveryapp.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
