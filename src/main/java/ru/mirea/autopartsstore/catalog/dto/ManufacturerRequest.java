package ru.mirea.autopartsstore.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManufacturerRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 100)
        String country,

        @Size(max = 255)
        String website

) {
}