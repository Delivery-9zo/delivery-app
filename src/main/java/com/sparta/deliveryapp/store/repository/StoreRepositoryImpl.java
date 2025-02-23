package com.sparta.deliveryapp.store.repository;


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deliveryapp.category.entity.QCategory;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.entity.QStore;
import com.sparta.deliveryapp.store.entity.QStoreCategory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public class StoreRepositoryImpl implements StoreRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public StoreRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  //todo: 리뷰 계산 추가하기
  @Override
  public Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude, int range, Pageable pageable) {

    QStore store = QStore.store;

    //todo: JPAQuery<Tuple> 형태로 변경하기

    // 쿼리 결과 반환 및 결과 개수 한번에 저장
    QueryResults<Tuple> results = queryFactory
        .select(store.storeId, store.storeName, store.address, store.bRegiNum, store.openAt,
            store.closeAt,
            Expressions.numberTemplate(Double.class,
                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
                longitude, latitude,
                store.storeCoordX,
                store.storeCoordY
            ).as("distanceFromRequest"))
        .from(store)
        .where(geoDistance(longitude, latitude, store.storeCoordX, store.storeCoordY, range))
//        .orderBy(Expressions.numberTemplate(Double.class,
//            "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
//            longitude, latitude,
//            store.storeCoordX,
//            store.storeCoordY
//        ).asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetchResults();

    List<StoreNearbyStoreResponseDto> storeNearbyStoreResponseDtos = results.getResults().stream()
        .map(tuple -> StoreNearbyStoreResponseDto.builder()
            .storeId(tuple.get(0, UUID.class))  // storeId
            .storeName(tuple.get(1, String.class))  // storeName
            .address(tuple.get(2, String.class))  // address
            .bRegiNum(tuple.get(3, String.class)) // bRegiNum
            .openAt(tuple.get(4, LocalTime.class))  // openAt
            .closeAt(tuple.get(5, LocalTime.class)) // closeAt
            .distanceFromRequest(tuple.get(6, Double.class)) // distanceFromRequest
            .build()
        )
        .toList();

    return new PageImpl<>(storeNearbyStoreResponseDtos, pageable, results.getTotal());
  }


  //todo: 리뷰 테이블과 연결하고 리뷰 컬럼 계산 추가하기
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

    JPAQuery<Tuple> query = queryFactory
        .select(
            store.storeId,
            store.storeName,
            store.address,
            store.bRegiNum,
            store.openAt,
            store.closeAt,
            Expressions.numberTemplate(
                Double.class,
                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
                longitude, latitude,
                store.storeCoordX,
                store.storeCoordY
            ).as("distanceFromRequest")
//            Expressions.numberTemplate(
//                Double.class,
//                "COALESCE(AVG(review.rating), 0)",
//                review.rating
//            ).as("averageRating")
        )
        .from(store)
        .innerJoin(store.storeCategories, storeCategory).fetchJoin()
        .innerJoin(storeCategory.category, category).fetchJoin()
//        .leftJoin(store.reviews, review)
        .where(
            category.categoryName.in(categoryNames),
            Expressions.numberTemplate(
                Double.class,
                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
                longitude, latitude,
                store.storeCoordX,
                store.storeCoordY
            ).lt(range)
        )
//        .orderBy(
//            Expressions.numberTemplate(
//                Double.class,
//                "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
//                longitude, latitude,
//                store.storeCoordX,
//                store.storeCoordY
//            ).asc()
//        )
        .groupBy(store.storeId)
        .distinct() // 중복 제거
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    QueryResults<Tuple> results = query.fetchResults();

    List<StoreNearbyStoreWithCategoryResponseDto> content = results.getResults().stream()
        .map(tuple -> StoreNearbyStoreWithCategoryResponseDto.builder()
            .storeId(tuple.get(store.storeId))
            .storeName(tuple.get(store.storeName))
            .address(tuple.get(store.address))
            .bRegiNum(tuple.get(store.bRegiNum))
            .openAt(tuple.get(store.openAt))
            .closeAt(tuple.get(store.closeAt))
            .distanceFromRequest(tuple.get(6, Double.class))
            .categories(categoryNames)
//            .rating(tuple.get(7, Double.class))
            .build())
        .toList();

    return new PageImpl<>(content, pageable, results.getTotal());
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
