package com.sparta.deliveryapp.order.repository;

import com.sparta.deliveryapp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderIdAndUserId(UUID orderId, UUID userId);

    List<Order> findByUserId(UUID userId);

    Optional<Order> findByOrderId(UUID orderId);

    @Query("SELECT o FROM Order o WHERE o.orderType <> 'CUSTOMER'")
    Page<Order> findAllByCreatedAt(Pageable pageable);
}
