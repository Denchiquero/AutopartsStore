package ru.mirea.autopartsstore.vin.dto;

import java.math.BigDecimal;

public record DecodedVin(

        String vin,

        String make,
        String model,
        Integer year,

        String engineCode,
        BigDecimal engineVolume,

        Integer power,

        String fuelType,
        String transmission,
        String driveType,
        String bodyType

) {
}