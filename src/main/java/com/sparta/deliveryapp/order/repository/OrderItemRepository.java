package com.sparta.deliveryapp.order.repository;

import com.sparta.deliveryapp.order.entity.Order;
import com.sparta.deliveryapp.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderId(Order order);

    @Modifying
    @Query("UPDATE OrderItem i SET i.deletedAt = current_timestamp, i.deletedBy = :deletedBy WHERE i.itemId = :itemId")
    void deleteOrderItem(@Param("deleteBy") String deletedBy, @Param("itemId") UUID itemId);

    @Query("SELECT oi FROM OrderItem oi LEFT JOIN oi.orderId o WHERE o.orderId = :orderId")
    List<OrderItem> findAllByOrderId(@Param("orderId") UUID orderId);
}
