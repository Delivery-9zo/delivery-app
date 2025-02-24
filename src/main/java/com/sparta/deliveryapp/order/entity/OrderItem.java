package com.sparta.deliveryapp.order.entity;

import com.sparta.deliveryapp.auditing.BaseEntity;
import com.sparta.deliveryapp.menu.entity.Menu;
import com.sparta.deliveryapp.order.dto.OrderItemRequestDto;
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

    @ManyToOne
    @JoinColumn(name = "order_uuid")
    private Order orderId;

    @ManyToOne
    @JoinColumn(name = "menu_uuid", nullable = false)
    private Menu menuId;

    @Column(name = "quantity")
    private int quantity;


    public OrderItem(Order order, Menu menu, int quantity) {
        this.orderId = order;
        this.menuId = menu;
        this.quantity = quantity;
    }

    public SearchOrderItemResponseDto toSearchOrderItemResponseDto(OrderItem orderItem) {
        return SearchOrderItemResponseDto.builder()
                .itemId(orderItem.getItemId())
                .menuId(orderItem.getMenuId())
                .quantity(orderItem.getQuantity())
                .build();
    }


}
