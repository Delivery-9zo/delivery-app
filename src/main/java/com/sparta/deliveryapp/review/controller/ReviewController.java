package com.sparta.deliveryapp.review.controller;

import com.sparta.deliveryapp.review.dto.ReviewGetResponseDto;
import com.sparta.deliveryapp.review.dto.ReviewPostRequestDto;
import com.sparta.deliveryapp.review.dto.ReviewSwaggerExample;
import com.sparta.deliveryapp.review.service.ReviewService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리뷰 API", description = "리뷰 CRUD 관련 API 엔드포인트")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(summary = "리뷰 작성", description = "완료된 주문건에 대한 리뷰를 등록합니다.")
  @ApiResponse(responseCode = "200", description = "리뷰 등록 성공", content = @Content(
      schema = @Schema(type = "string", example = "리뷰가 등록되었습니다."),
      mediaType = "application/json"
  ))
  @PostMapping("/{orderId}")
  public ResponseEntity<String> postReview(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ReviewPostRequestDto.class)
          )
      )
      @RequestBody ReviewPostRequestDto dto,
      @PathVariable(name = "orderId") UUID orderID,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    reviewService.postReview(dto, orderID, userDetails);
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body("리뷰가 등록되었습니다.");
  }

  @Operation(summary = "가게의 모든 리뷰 조회", description = "가게의 모든 리뷰를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "가게의 모든 리뷰 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "페이징 처리된 리뷰 목록",
          value = ReviewSwaggerExample.STORE_REVIEW_LIST_EXAMPLE
      )))
  @Parameter(name = "storeId", description = "리뷰 조회할 상점의 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
  @Parameter(name = "size", description = "페이지 크기", example = "10")
  @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
  @Parameter(name = "sort", description = "정렬 기준 필드 (예: createdAt,asc 또는 createdAt,desc)",
      example = "createdAt,desc")
  @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ROLE_OWNER', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
  @GetMapping("/{storeId}")
  public ResponseEntity<Page<ReviewGetResponseDto>> getAllReviewsByStore(
      @PathVariable(name = "storeId") UUID storeId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
      @Parameter(hidden = true) Pageable pageable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getAllReviewsByStore(storeId, pageable));
  }

  @Operation(summary = "유저의 모든 리뷰 조회", description = "유저가 작성한 모든 리뷰를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "유저의 모든 리뷰 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "페이징 처리된 리뷰 목록",
          value = ReviewSwaggerExample.USER_REVIEW_LIST_EXAMPLE
      )))
  @Parameter(name = "size", description = "페이지 크기", example = "10")
  @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
  @Parameter(name = "sort", description = "정렬 기준 필드 (예: createdAt,asc 또는 createdAt,desc)",
      example = "createdAt,desc")
  @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'ROLE_OWNER', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
  @GetMapping
  public ResponseEntity<Page<ReviewGetResponseDto>> getAllReviewsByCustomer(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
      @Parameter(hidden = true) Pageable pageable) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getAllReviewsByCustomer(userDetails, pageable));
  }

  @Operation(summary = "가게의 리뷰 평점 조회", description = "가게의 리뷰 평점을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "가게의 리뷰 평점 조회 성공", content = @Content(
      schema = @Schema(type = "number", example = "3.5"),
      mediaType = "application/json",
      examples = @ExampleObject(
          name = "페이징 처리된 리뷰 목록",
          value = ReviewSwaggerExample.USER_REVIEW_LIST_EXAMPLE
      )
  ))
  @Parameter(name = "storeId", description = "평점 조회할 가게 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
  @GetMapping("/{storeId}/avgRating")
  public ResponseEntity<Double> getStoreAvgRating(@PathVariable UUID storeId) {

    return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(reviewService.getStoreAvgRating(storeId));


  }

  @Operation(summary = "리뷰 삭제", description = "관리자 권한 요청 시 리뷰를 삭제합니다.")
  @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공",
      content = @Content(schema = @Schema(type = "string", example = "리뷰가 삭제되었습니다.")))
  @Parameter(name = "reviewId", description = " 삭제할 리뷰 Id", required = true, example =
      "730320d6-cd32-4ddc-b56b-78f810d7d543")
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
