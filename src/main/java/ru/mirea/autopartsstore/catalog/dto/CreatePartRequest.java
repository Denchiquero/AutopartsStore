package ru.mirea.autopartsstore.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePartRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 50)
        String sku,

        @NotBlank
        @Size(max = 100)
        String article,

        @Size(max = 1000)
        String description,

        @NotNull
        @PositiveOrZero
        BigDecimal price,

        @NotNull
        Long manufacturerId,

        @NotNull
        Long categoryId

) {
}
