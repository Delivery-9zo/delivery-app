package com.sparta.deliveryapp.payment.repository;

import com.sparta.deliveryapp.payment.entity.Payment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<Payment> findByOrderId(UUID orderId);

    @Modifying
    @Query("UPDATE Payment p SET p.deletedAt = current_timestamp, p.deletedBy = :deletedBy WHERE p.paymentId = :paymentId")
    void deletePayment(@Param("deletedBy") String deletedBy, @Param("paymentId") UUID paymentId);


    @Modifying
    @Transactional
    @Query("UPDATE Payment s SET s.deletedAt = CURRENT_TIMESTAMP, s.deletedBy = :deletedBy WHERE s.userId = :userId")
    void deletePaymentByUserId(@Param("deletedBy") String deletedBy, @Param("userId") UUID userId);

}
