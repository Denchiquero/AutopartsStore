package ru.mirea.autopartsstore.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InventoryOperationRequest(

        @NotNull
        @Positive
        @Schema(example = "10")
        Integer quantity,

        @Size(max = 500)
        @Schema(example = "10")
        String comment

) {
}