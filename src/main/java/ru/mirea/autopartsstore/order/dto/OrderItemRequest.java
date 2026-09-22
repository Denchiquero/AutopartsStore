package ru.mirea.autopartsstore.order.dto;

import jakarta.validation.constraints.*;

public record OrderItemRequest(

        @NotNull
        Long partId,

        @NotNull
        @Positive
        Integer quantity

) {
}