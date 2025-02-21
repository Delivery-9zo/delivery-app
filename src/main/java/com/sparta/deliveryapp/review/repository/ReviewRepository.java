package com.sparta.deliveryapp.review.repository;

import com.sparta.deliveryapp.review.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

  Page<Review> findAllByStore_StoreId(UUID storeId, Pageable pageable);

  Page<Review> findAllByUser_Email(String email, Pageable pageable);
}
