package ru.mirea.autopartsstore.order.dto;

import jakarta.validation.constraints.NotNull;
import ru.mirea.autopartsstore.order.entity.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatus status
) {
}