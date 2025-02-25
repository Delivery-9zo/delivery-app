package com.sparta.deliveryapp.review.repository.querydsl;

import java.util.UUID;

public interface ReviewRepositoryCustom {

  double getAvgRatingByStoreId(UUID storeId);
}
