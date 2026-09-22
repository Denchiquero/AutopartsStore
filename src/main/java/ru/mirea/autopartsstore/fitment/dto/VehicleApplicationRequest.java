package ru.mirea.autopartsstore.fitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VehicleApplicationRequest(

        @NotBlank
        @Size(max = 100)
        String make,

        @NotBlank
        @Size(max = 100)
        String model,

        @Size(max = 100)
        String generation,

        @NotNull
        Integer yearFrom,

        Integer yearTo,

        @Size(max = 50)
        String engineCode,

        @Positive
        BigDecimal engineVolume,

        @Positive
        Integer power,

        @Size(max = 30)
        String fuelType,

        @Size(max = 30)
        String transmission,

        @Size(max = 30)
        String driveType,

        @Size(max = 30)
        String bodyType

) {
}