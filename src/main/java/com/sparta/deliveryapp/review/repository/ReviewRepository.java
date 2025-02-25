package com.sparta.deliveryapp.review.repository;

import com.sparta.deliveryapp.review.entity.Review;
import com.sparta.deliveryapp.review.repository.querydsl.ReviewRepositoryCustom;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends
    JpaRepository<Review, UUID>,
    ReviewRepositoryCustom {

  Page<Review> findAllByStore_StoreId(UUID storeId, Pageable pageable);

  Page<Review> findAllByUser_Email(String email, Pageable pageable);


  @Modifying
  @Transactional
  @Query("UPDATE Review s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.user.userId = :userId")
  void deleteReviewByUser(@Param("deletedBy") String deletedBy, @Param("userId") UUID userId);

}
