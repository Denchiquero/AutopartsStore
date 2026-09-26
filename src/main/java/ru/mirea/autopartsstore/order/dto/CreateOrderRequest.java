package ru.mirea.autopartsstore.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty
        @Valid
        List<OrderItemRequest> items

) {
}
