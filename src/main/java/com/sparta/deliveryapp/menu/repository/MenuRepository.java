package com.sparta.deliveryapp.menu.repository;

import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.store.entity.Store;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

  Optional<Menu> findByNameAndStore_StoreId(String name, UUID storeId);

  Page<Menu> findAllByStore_StoreId(UUID storeId, Pageable pageable);

  void deleteByIdAndStore_StoreId(UUID menuId, UUID storeId);

  List<Menu> findAllByStore(Store store);
}
