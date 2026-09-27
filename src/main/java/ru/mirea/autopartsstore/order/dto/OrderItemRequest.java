package ru.mirea.autopartsstore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record OrderItemRequest(

        @NotNull
        @Schema(
                description = "ID запчасти",
                example = "1"
        )
        Long partId,

        @NotNull
        @Positive
        @Schema(
                description = "Количество",
                example = "2"
        )
        Integer quantity

) {
}