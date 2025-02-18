package com.sparta.deliveryapp.store.repository;

import com.sparta.deliveryapp.store.entity.StoreEntity;
import com.sparta.deliveryapp.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {

  Optional<StoreEntity> findByStoreName(@NotBlank String storeName);

  List<StoreEntity> findByUser(User user);

}
