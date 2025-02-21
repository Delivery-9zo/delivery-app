package com.sparta.deliveryapp.review.repository;

import com.sparta.deliveryapp.review.entity.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

}
