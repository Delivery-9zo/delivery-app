package com.sparta.deliveryapp.order.repository;

import com.sparta.deliveryapp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderIdAndUserId(UUID orderId, UUID userId);
    Optional<Order> findByOrderId(UUID orderId);

    List<Order> findAllByUserId(UUID userId);
    Page<Order> findByUserId(Pageable pageable, UUID userId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
