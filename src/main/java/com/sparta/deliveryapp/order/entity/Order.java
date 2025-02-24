package com.sparta.deliveryapp.order.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.order.dto.SearchOrderItemResponseDto;
import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE p_order SET deleted_at=CURRENT_TIMESTAMP WHERE order_id=?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID orderId;

    // 주문타입 - FACE_TO_FACE: 불필요
    @Column(name = "user_id")
    private UUID userId;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "store_uuid")
    private Store store;

    @Builder
    public Order(UUID userId, OrderType orderType, int totalPrice, String userAddress, String orderMemo) {
        this.userId = userId;
        this.orderType = orderType;
        this.orderTime = LocalDateTime.now();
        this.totalPrice = totalPrice;
        this.userAddress = userAddress;
        this.orderMemo = orderMemo;
        this.orderState = OrderState.WAIT;
    }

    public SearchOrderResponseDto toSearchOrderByOrderIdResponseDto(Order order, List<SearchOrderItemResponseDto> itemList) {
        return SearchOrderResponseDto.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .orderType(order.getOrderType())
                .orderTime(order.getOrderTime())
                .totalPrice(order.getTotalPrice())
                .userAddress(order.getUserAddress())
                .orderMemo(order.getOrderMemo())
                .orderState(order.getOrderState())
                .build();
    }

}
