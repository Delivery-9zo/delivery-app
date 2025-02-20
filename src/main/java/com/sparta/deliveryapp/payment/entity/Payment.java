package com.sparta.deliveryapp.payment.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator()
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @CreationTimestamp
    @Column(name = "payment_time", updatable = false, nullable = false)
    private LocalDateTime paymentTime;
}
