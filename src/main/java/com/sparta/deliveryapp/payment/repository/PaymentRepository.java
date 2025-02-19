package com.sparta.deliveryapp.payment.repository;

import com.sparta.deliveryapp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
