package ru.mirea.autopartsstore.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InventoryOperationRequest(

        @NotNull
        @Positive
        Integer quantity,

        @Size(max = 500)
        String comment

) {
}