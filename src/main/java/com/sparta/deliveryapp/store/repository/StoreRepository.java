package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.entity.Store;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

  Optional<Store> findByStoreName(@NotBlank String storeName);

  List<Store> findByUser(User user);

  Optional<Store> findByStoreId(UUID uuid);
}
