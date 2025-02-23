package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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


}

interface StoreRepositoryCustom {

  Page<StoreNearbyStoreResponseDto> findNearbyStoresWithoutCategory(double longitude,
      double latitude, int range,
      Pageable pageable);

  Page<StoreNearbyStoreWithCategoryResponseDto> findNearbyStoresByCategories(
      List<String> categoryNames,
      double longitude,
      double latitude,
      int range,
      Pageable pageable);
}
