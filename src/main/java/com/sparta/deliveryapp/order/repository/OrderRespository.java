package com.sparta.deliveryapp.order.repository;

import com.sparta.deliveryapp.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRespository extends JpaRepository<Order, UUID> {
}
