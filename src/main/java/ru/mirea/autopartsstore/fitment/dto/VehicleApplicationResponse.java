package ru.mirea.autopartsstore.fitment.dto;

import java.math.BigDecimal;

public record VehicleApplicationResponse(

        Long id,

        String make,
        String model,
        String generation,

        Integer yearFrom,
        Integer yearTo,

        String engineCode,
        BigDecimal engineVolume,
        Integer power,

        String fuelType,
        String transmission,
        String driveType,
        String bodyType

) {
}