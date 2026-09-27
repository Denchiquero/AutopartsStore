package ru.mirea.autopartsstore.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePartRequest(

        @NotBlank
        @Size(max = 150)
        @Schema(example = "Масляный фильтр")
        String name,

        @NotBlank
        @Size(max = 50)
        @Schema(example = "MANN-W712")
        String sku,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "MANN-W712")
        String article,

        @Size(max = 1000)
        @Schema(example = "W712/95")
        String description,

        @NotNull
        @PositiveOrZero
        @Schema(example = "850.00")
        BigDecimal price,

        @NotNull
        @Schema(example = "1")
        Long manufacturerId,

        @NotNull
        @Schema(example = "2")
        Long categoryId

) {
}
