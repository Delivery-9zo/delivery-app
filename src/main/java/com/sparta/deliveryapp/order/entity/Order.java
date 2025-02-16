package com.sparta.deliveryapp.order.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_order")
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID orderId;

    // 주문타입 - FACE_TO_FACE: 불필요
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    @CreationTimestamp
    @Column(name = "order_time", updatable = false, nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "total_price")
    private int totalPrice;

    /*
    * 주문타입별
    * - FACE_TO_FACE: 불필요
    * - NON_FACE_TO_FACE: 필수
    */
    @Column(name = "user_address")
    private String userAddress;

    @Column(name = "order_memo")
    private String orderMemo;

    @Enumerated(value = EnumType.STRING)
    private OrderState orderState;
}
