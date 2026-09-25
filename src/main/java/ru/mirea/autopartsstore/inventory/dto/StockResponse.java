package ru.mirea.autopartsstore.inventory.dto;

import java.math.BigDecimal;

public record StockResponse(

        Long partId,
        String sku,
        String partName,
        String manufacturer,
        String category,
        BigDecimal price,
        Integer quantity

) {
}