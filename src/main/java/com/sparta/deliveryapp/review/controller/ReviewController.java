package com.sparta.deliveryapp.review.controller;

import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.service.ReviewService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/{storeId}")
  public ResponseEntity<String> postReview(
      @RequestBody ReviewPostRequestDto dto,
      @PathVariable(name = "storeId") UUID storeId,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    reviewService.postReview(dto, storeId, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("리뷰가 등록되었습니다.");
  }

}
