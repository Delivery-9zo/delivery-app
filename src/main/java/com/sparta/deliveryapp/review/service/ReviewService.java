package com.sparta.deliveryapp.review.service;

import com.sparta.deliveryapp.review.dto.ReviewGetResponseDto;
import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.entity.Review;
import com.sparta.deliveryapp.review.repository.ReviewRepository;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.store.repository.StoreRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

  public Page<ReviewGetResponseDto> getAllReviewsByStore(UUID storeId,
      Pageable pageable
  ) {
    return reviewRepository.findAllByStore_StoreId(storeId, pageable)
        .map(review -> new ReviewGetResponseDto(
            review.getId(),
            review.getComment(),
            review.getStore().getStoreId(),
            review.getImage(),
            review.getRating()
        ));
  }

  public Page<ReviewGetResponseDto> getAllReviewsByCustomer(UserDetailsImpl userDetails,
      Pageable pageable) {
    return reviewRepository.findAllByUser_Email(userDetails.getEmail(), pageable)
        .map(review -> new ReviewGetResponseDto(
            review.getId(),
            review.getComment(),
            review.getStore().getStoreId(),
            review.getImage(),
            review.getRating()
        ));
  }

  public Double getStoreAvgRating(UUID storeId) {
    return reviewRepository.getAvgRatingByStoreId(storeId);
  }

  public void deleteReview(UUID reviewId) {
    reviewRepository.deleteById(reviewId);
  }
}
