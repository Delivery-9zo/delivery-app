package com.sparta.deliveryapp.order.repository;

import com.sparta.deliveryapp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderIdAndUserId(UUID orderId, UUID userId);
    Optional<Order> findByOrderId(UUID orderId);

    List<Order> findAllByUserId(UUID userId);
    Page<Order> findByUserId(Pageable pageable, UUID userId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.deletedAt = current_timestamp, o.deletedBy = :deteedBy WHERE o.orderId = :orderId")
    void deleteOrder(@Param("deleteBy") String deletedBy, @Param("orderId") UUID orderId);
}
