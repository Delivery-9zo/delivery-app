package com.sparta.deliveryapp.payment.repository;

import com.sparta.deliveryapp.payment.entity.Payment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findAllByOrderId(UUID userId);
}
