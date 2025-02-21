package com.sparta.deliveryapp.order.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID orderId;

    // 주문타입 - FACE_TO_FACE: 불필요
    @Column(name = "user_id")
    private UUID userId;

    // TODO 삭제 예정
    @Column(name = "item_id")
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

    @OneToMany
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "payment_paymentId")
    private Payment payment;

    @Builder
    public Order(UUID userId, UUID itemId, OrderType orderType, int totalPrice, String userAddress, String orderMemo) {
        this.userId = userId;
        this.itemId = itemId;
        this.orderType = orderType;
        this.orderTime = LocalDateTime.now();
        this.totalPrice = totalPrice;
        this.userAddress = userAddress;
        this.orderMemo = orderMemo;
        this.orderState = OrderState.WAIT;
    }
}
