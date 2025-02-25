package com.sparta.deliveryapp.review.repository;

import com.sparta.deliveryapp.review.entity.Review;
import com.sparta.deliveryapp.review.repository.querydsl.ReviewRepositoryCustom;
import com.sparta.deliveryapp.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends
    JpaRepository<Review, UUID>,
    ReviewRepositoryCustom {

  Page<Review> findAllByStore_StoreId(UUID storeId, Pageable pageable);

  Page<Review> findAllByUser_Email(String email, Pageable pageable);

  List<Review> findAllByUser(User findUser);
}
