package com.sparta.deliveryapp.review.controller;

import com.sparta.deliveryapp.review.dto.ReviewGetResponseDto;
import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.service.ReviewService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/{storeId}")
  public ResponseEntity<String> postReview(@RequestBody ReviewPostRequestDto dto,
      @PathVariable(name = "storeId") UUID storeId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    reviewService.postReview(dto, storeId, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("리뷰가 등록되었습니다.");
  }

  @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ROLE_OWNER', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
  @GetMapping("/{storeId}")
  public ResponseEntity<Page<ReviewGetResponseDto>> getAllReviewsByStore(
      @PathVariable(name = "storeId") UUID storeId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getAllReviewsByStore(storeId, pageable));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ROLE_OWNER', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
  @GetMapping
  public ResponseEntity<Page<ReviewGetResponseDto>> getAllReviewsByCustomer(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getAllReviewsByCustomer(userDetails, pageable));
  }

  @GetMapping("/{storeId}/avgRating")
  public ResponseEntity<Double> getStoreAvgRating(@PathVariable UUID storeId) {

    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getStoreAvgRating(storeId));


  }

  @PreAuthorize("hasAuthority('ROLE_MASTER')")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<String> deleteReview(@PathVariable(name = "reviewId") UUID reviewId) {
    reviewService.deleteReview(reviewId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("리뷰가 삭제되었습니다.");
  }
}
