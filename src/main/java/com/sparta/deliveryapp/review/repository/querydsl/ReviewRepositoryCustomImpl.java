package com.sparta.deliveryapp.review.repository.querydsl;

import com.sparta.deliveryapp.review.entity.QReview;
import com.sparta.deliveryapp.review.entity.Review;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;
import java.util.UUID;

public class ReviewRepositoryCustomImpl extends QuerydslRepositorySupport
    implements ReviewRepositoryCustom {

  public ReviewRepositoryCustomImpl() {
    super(Review.class);
  }

  @Override
  public double getAvgRatingByStoreId(UUID storeId) {
    QReview review = QReview.review;

    return Optional.ofNullable(
            from(review)
                .select(review.rating.avg())
                .where(review.store.storeId.eq(storeId))
                .fetchOne())
        .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));
  }
}
