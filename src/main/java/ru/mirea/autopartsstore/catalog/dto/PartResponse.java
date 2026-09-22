package ru.mirea.autopartsstore.catalog.dto;

import java.math.BigDecimal;

public record PartResponse(

        Long id,
        String name,
        String sku,
        String article,
        String description,
        BigDecimal price,

        Long manufacturerId,
        String manufacturerName,

        Long categoryId,
        String categoryName

) {
}