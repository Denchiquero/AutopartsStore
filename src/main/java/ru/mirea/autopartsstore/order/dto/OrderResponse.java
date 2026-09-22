package ru.mirea.autopartsstore.order.dto;

import ru.mirea.autopartsstore.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(

        Long id,

        Long customerId,
        String customerName,

        LocalDateTime createdAt,
        OrderStatus status,

        BigDecimal totalPrice,

        List<OrderItemResponse> items

) {
}
