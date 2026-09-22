package ru.mirea.autopartsstore.inventory.dto;

public record StockResponse(

        Long partId,
        String sku,
        String partName,
        Integer quantity

) {
}