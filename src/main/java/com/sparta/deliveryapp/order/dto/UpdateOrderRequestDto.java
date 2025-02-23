package com.sparta.deliveryapp.order.dto;

import com.sparta.deliveryapp.order.entity.OrderState;
import com.sparta.deliveryapp.order.entity.OrderType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateOrderRequestDto {

    @Parameter(name = "order_id", description = "주문 uuid", required = true)
    @Schema(description = "사용자의 주문 uuid를 입력하세요.", example = "45a039a0-0142-4b3a-b30f-0a156900d775")
    @NotNull(message = "Order Id cannot be null")
    private UUID orderId;

    @Parameter(name = "order_state", description = "주문 상태", required = true)
    @Schema(description = "사용자의 주문 상태가 필요합니다.", example = "WAIT, SUCCESS, CANCEL")
    @NotNull(message = "OrderState cannot be null")
    private OrderState orderState;

    @Parameter(name = "total_price", description = "총주문금액", required = true)
    @Schema(description = "사용자의 총주문금액이 필요합니다.", example = "12000")
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 1, message = "결제 금액은 1원 이상 입력되어야 합니다.")
    private int totalPrice;

    @Parameter(name = "order_type", description = "총주문금액", required = true)
    @Schema(description = "사용자의 주문 타입이 필요합니다.", example = "NON_FACE_TO_FACE, FACE_TO_FACE")
    private OrderType orderType;
}
