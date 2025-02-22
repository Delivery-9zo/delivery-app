package com.sparta.deliveryapp.store.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.entity.QStore;
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

  @Override
  public Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude, int range, Pageable pageable) {

    QStore store = QStore.store;

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
        .orderBy(Expressions.numberTemplate(Double.class,
            "ST_Distance(geography(ST_SetSRID(ST_Point({0}, {1}), 4326)), geography(ST_SetSRID(ST_Point({2}, {3}), 4326)))",
            longitude, latitude,
            store.storeCoordX,
            store.storeCoordY
        ).asc())
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

    // Page 객체로 반환, 결과와 totalCount를 포함
    return new PageImpl<>(storeNearbyStoreResponseDtos, pageable, results.getTotal());
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
