package ru.mirea.autopartsstore.vin.dto;

import ru.mirea.autopartsstore.catalog.dto.PartResponse;

import java.util.List;

public record VinPartsResponse(
        DecodedVin vehicle,
        Long vehicleApplicationId,
        List<PartResponse> parts
) {
}