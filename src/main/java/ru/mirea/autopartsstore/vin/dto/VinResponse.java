package ru.mirea.autopartsstore.vin.dto;

import java.math.BigDecimal;

public record VinResponse(
        Long vehicleId,
        String vin,
        String make,
        String model,
        String generation,
        Integer modelYear,
        String engineCode,
        BigDecimal engineVolume,
        Integer power,
        String fuelType,
        String transmission,
        String driveType,
        String bodyType
) {
}