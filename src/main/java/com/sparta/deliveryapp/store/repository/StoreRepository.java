package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

  Optional<Store> findByStoreName(@NotBlank String storeName);

  List<Store> findByUser(User user);

  Optional<Store> findByStoreId(UUID uuid);

  Page<Store> findByStoreNameContaining(String storeName, Pageable pageable);

  @Query(value = "SELECT s FROM Store s LEFT JOIN FETCH s.storeCategories sc "
      + "LEFT JOIN FETCH sc.category c WHERE s.storeName LIKE %:storeName%", countQuery =
      "SELECT COUNT(s) FROM Store s LEFT JOIN s.storeCategories sc "
          + "LEFT JOIN sc.category c WHERE s.storeName LIKE %:storeName%")
  Page<Store> findByStoreNameContainingWithCategories(@Param("storeName") String storeName,
      Pageable pageable);

  @Modifying
  @Transactional
  @Query("UPDATE Store s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.user.userId = :userId")
  void deleteStoreByUserId(@Param("deletedBy") String deletedBy, @Param("userId") UUID userId);

  @Modifying
  @Transactional
  @Query("UPDATE Store s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.storeId = :storeId")
  void deleteStoreByStoreId(@Param("deletedBy") String deletedBy, @Param("storeId") UUID storeId);

  List<Store> findAllByUser(User user);
}

interface StoreRepositoryCustom {

  Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude, int range, Pageable pageable);

  Page<StoreNearbyStoreWithCategoryResponseDto> findNearbyStoresByCategories(
      List<String> categoryNames, double longitude, double latitude, int range, Pageable pageable);

}
