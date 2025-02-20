package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
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
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

  Optional<Store> findByStoreName(@NotBlank String storeName);

  List<Store> findByUser(User user);

  Optional<Store> findByStoreId(UUID uuid);

  List<Store> findByStoreNameContaining(String storeName);

//  @Query(value = """
//      SELECT
//        s.store_id AS storeId,
//        s.store_name AS storeName,
//        s.address AS address,
//        s.b_regi_num AS bRegiNum,
//        s.open_at AS openAt,
//        s.close_at AS closeAt,
//        ST_Distance(
//            geography(ST_SetSRID(ST_Point(s.store_x, s.store_y), 4326)),
//            geography(ST_SetSRID(ST_Point(:longitude, :latitude), 4326))
//        ) AS distanceFromRequest
//      FROM p_store s
//      WHERE ST_DWithin(
//          geography(ST_SetSRID(ST_Point(s.store_x, s.store_y), 4326)),
//          geography(ST_SetSRID(ST_Point(:longitude, :latitude), 4326)),
//          :range
//      )
//""", nativeQuery = true)
//  List<Object[]> findNearbyStoresWithoutCategory(@Param("longitude") double longitude,
//      @Param("latitude") double latitude,
//      @Param("range") int range);

}

interface StoreRepositoryCustom {
  List<Object[]> findNearbyStoresWithoutCategory(double longitude, double latitude, int range);
}
