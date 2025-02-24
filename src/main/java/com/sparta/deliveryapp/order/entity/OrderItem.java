package com.sparta.deliveryapp.order.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.order.dto.SearchOrderItemResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_orderItem")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID itemId;

    // 주문타입 - FACE_TO_FACE: 불필요
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "menu_id", nullable = false)
    private UUID menuId;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public OrderItem(UUID userId, UUID menuId, int quantity, Order order) {
        this.userId = userId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.order = order;
    }

    public SearchOrderItemResponseDto toSearchOrderItemResponseDto(OrderItem orderItem) {
        return SearchOrderItemResponseDto.builder()
                .itemId(orderItem.getItemId())
                .menuId(orderItem.getMenuId())
                .quantity(orderItem.getQuantity())
                .build();
    }


}
