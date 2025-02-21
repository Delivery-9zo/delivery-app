package com.sparta.deliveryapp.review.service;

import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.entity.Review;
import com.sparta.deliveryapp.review.repository.ReviewRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;

  public void postReview(ReviewPostRequestDto dto, UUID storeId, UserDetailsImpl userDetails) {
    Store store = storeRepository.getReferenceById(storeId);
    User user = userRepository.findByEmail(userDetails.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    Review review = Review.of(dto.comment(), store, user, dto.image(), dto.rating());
    reviewRepository.save(review);
  }
}
