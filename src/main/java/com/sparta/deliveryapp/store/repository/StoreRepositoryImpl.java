package com.sparta.deliveryapp.store.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deliveryapp.store.entity.QStore;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public class StoreRepositoryImpl implements StoreRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public StoreRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public List<Object[]> findNearbyStoresWithoutCategory(double longitude, double latitude,
      int range) {
    QStore store = QStore.store;

    List<Tuple> tuples = queryFactory
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
        .fetch();

    // Tuple을 Object[]로 변환
    return tuples.stream()
        .map(tuple -> new Object[] {
            tuple.get(0, UUID.class),                       // storeId
            tuple.get(1, String.class),                     // storeName
            tuple.get(2, String.class),                     // address
            tuple.get(3, String.class),                     // bRegiNum
            tuple.get(4, java.time.LocalTime.class), // openAt -> String으로 변환
            tuple.get(5, java.time.LocalTime.class), // closeAt -> String으로 변환
            tuple.get(6, Double.class)                      // distanceFromRequest
        })
        .toList();
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
