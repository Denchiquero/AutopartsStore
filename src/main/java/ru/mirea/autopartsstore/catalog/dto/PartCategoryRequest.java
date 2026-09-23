package ru.mirea.autopartsstore.catalog.dto;

import jakarta.validation.constraints.*;

public record PartCategoryRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description

) {
}
