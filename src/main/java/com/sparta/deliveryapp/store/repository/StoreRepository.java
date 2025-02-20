package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

  Optional<Store> findByStoreName(@NotBlank String storeName);

  List<Store> findByUser(User user);

  Optional<Store> findByStoreId(UUID uuid);

  List<Store> findByStoreNameContaining(String storeName);

  @Query(value = """
          SELECT *
          FROM p_store s
          WHERE ST_DWithin(
              geography(ST_SetSRID(ST_Point(s.store_x, s.store_y), 4326)),
              geography(ST_SetSRID(ST_Point(:longitude, :latitude), 4326)),
              :range
          )
      """, nativeQuery = true)
  List<Object[]> findNearbyStoresWithoutCategory(@Param("longitude") double longitude,
      @Param("latitude") double latitude,
      @Param("range") int range);


}
