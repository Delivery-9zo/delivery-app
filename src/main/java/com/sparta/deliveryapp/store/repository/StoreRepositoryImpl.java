package com.sparta.deliveryapp.store.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deliveryapp.category.entity.QCategory;
import com.sparta.deliveryapp.review.entity.QReview;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.entity.QStore;
import com.sparta.deliveryapp.store.entity.QStoreCategory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class StoreRepositoryImpl implements StoreRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public StoreRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude, int range, Pageable pageable) {

    QStore store = QStore.store;
    QReview review = QReview.review;

    BooleanExpression distanceCondition = geoDistance(longitude, latitude, store.storeCoordX,
        store.storeCoordY, range);

    // 쿼리 결과 반환 및 결과 개수 한번에 저장
    JPAQuery<Tuple> query = queryFactory
        .select(
            store.storeId,
            store.storeName,
            store.address,
            store.bRegiNum,
            store.openAt,
            store.closeAt,
            Expressions.numberTemplate(Double.class,
                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
                longitude, latitude,
                store.storeCoordX,
                store.storeCoordY
            ).as("distanceFromRequest"),
            review.rating.avg().as("averageRating"),
            store.createdAt,
            store.updatedAt
        )
        .from(store)
        .leftJoin(store.reviews, review)
        .where(distanceCondition)
        .groupBy(store.storeId)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    NumberTemplate<Double> distanceExpression = Expressions.numberTemplate(Double.class,
        "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
        longitude, latitude,
        store.storeCoordX,
        store.storeCoordY
    );

    List<StoreNearbyStoreResponseDto> contents = query.fetch().stream()
        .map(tuple -> StoreNearbyStoreResponseDto.builder()
            .storeId(tuple.get(store.storeId))  // storeId
            .storeName(tuple.get(store.storeName))  // storeName
            .address(tuple.get(store.address))  // address
            .bRegiNum(tuple.get(store.bRegiNum)) // bRegiNum
            .openAt(tuple.get(store.openAt))  // openAt
            .closeAt(tuple.get(store.closeAt)) // closeAt
            .distanceFromRequest(
                tuple.get(distanceExpression) != null ? tuple.get(distanceExpression)
                    : 0.0) // distanceFromRequest: null 체크 추가
            .rating(tuple.get(7, Double.class) == null ? 0.0 : tuple.get(7, Double.class)) // rating
            .createdAt(tuple.get(8, LocalDateTime.class))
            .updatedAt(tuple.get(9, LocalDateTime.class))
            .build()
        )
        .toList();

    return new PageImpl<>(contents, pageable, contents.size());
  }


  @Override
  public Page<StoreNearbyStoreWithCategoryResponseDto> findNearbyStoresByCategories(
      List<String> categoryNames,
      double longitude,
      double latitude,
      int range,
      Pageable pageable
  ) {
    QStore store = QStore.store;
    QStoreCategory storeCategory = QStoreCategory.storeCategory;
    QCategory category = QCategory.category;
    QReview review = QReview.review;

    BooleanExpression distanceCondition = geoDistance(longitude, latitude, store.storeCoordX,
        store.storeCoordY, range);

    BooleanExpression categoryCondition = category.categoryName.in(categoryNames);

    JPAQuery<Long> countQuery = queryFactory
        .select(store.countDistinct())
        .from(store)
        .innerJoin(store.storeCategories, storeCategory)
        .innerJoin(storeCategory.category, category)
        .leftJoin(store.reviews, review)
        .where(categoryCondition, geoDistance(longitude, latitude, store.storeCoordX,
            store.storeCoordY, range));

    Long totalCount = countQuery.fetchOne();

    List<Tuple> query = queryFactory
        .select(
            store.storeId,
            store.storeName,
            store.address,
            store.bRegiNum,
            store.openAt,
            store.closeAt,
            Expressions.numberTemplate(Double.class,
                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
                longitude, latitude,
                store.storeCoordX,
                store.storeCoordY
            ).as("distanceFromRequest"),
            review.rating.avg().as("rating"),
            store.createdAt,
            store.updatedAt,
            store.storeCategories,
            storeCategory.category,
            storeCategory,
            category
        )
        .from(store)
        .innerJoin(store.storeCategories, storeCategory)
        .innerJoin(storeCategory.category, category)
        .leftJoin(store.reviews, review)
        .where(categoryCondition, distanceCondition)
        .groupBy(store.storeId, storeCategory.storeCategoryId, category.categoryId)
        .distinct() // 중복 제거
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<StoreNearbyStoreWithCategoryResponseDto> contents = query.stream()
        .map(tuple -> StoreNearbyStoreWithCategoryResponseDto.builder()
            .storeId(tuple.get(store.storeId))
            .storeName(tuple.get(store.storeName))
            .address(tuple.get(store.address))
            .bRegiNum(tuple.get(store.bRegiNum))
            .openAt(tuple.get(store.openAt))
            .closeAt(tuple.get(store.closeAt))
            .distanceFromRequest(tuple.get(6, Double.class))
            .rating(tuple.get(7, Double.class) == null ? 0.0 : tuple.get(7, Double.class))
            .createdAt(tuple.get(8, LocalDateTime.class))
            .updatedAt(tuple.get(9, LocalDateTime.class))
            .categories(categoryNames)
            .build())
        .toList();

    return new PageImpl<>(contents, pageable, totalCount);
  }

  private BooleanExpression geoDistance(double longitude, double latitude,
      NumberPath<Double> storeX, NumberPath<Double> storeY, int range) {

    return Expressions.numberTemplate(Double.class,
        "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
        longitude, latitude,
        storeX,
        storeY
    ).loe((double) range);
  }
}
