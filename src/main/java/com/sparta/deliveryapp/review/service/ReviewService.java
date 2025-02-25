package com.sparta.deliveryapp.review.service;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.repository.OrderRepository;
import com.sparta.deliveryapp.review.dto.ReviewGetResponseDto;
import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.entity.Review;
import com.sparta.deliveryapp.review.repository.ReviewRepository;
import com.sparta.deliveryapp.user.entity.User;
import com.sparta.deliveryapp.user.repository.UserRepository;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;

  @Transactional
  public void postReview(ReviewPostRequestDto dto, UUID orderId, UserDetailsImpl userDetails) {
    Order order = orderRepository.getReferenceById(orderId);

    if (order.getOrderState() != OrderState.SUCCESS) {
      throw new CustomException(ErrorCode.REVIEW_NOT_ORDER_SUCCESS);
    }

    User user = userRepository.findByEmail(userDetails.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_EXISTS_USER_ID));
    Review review = Review.of(dto.comment(), order.getStore(), user, dto.image(), dto.rating());
    reviewRepository.save(review);
  }

  @Transactional
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

  @Transactional
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

  @Transactional
  public Double getStoreAvgRating(UUID storeId) {
    return reviewRepository.getAvgRatingByStoreId(storeId);
  }

  @Transactional
  public void deleteReview(UUID reviewId) {
    reviewRepository.deleteById(reviewId);
  }
}
