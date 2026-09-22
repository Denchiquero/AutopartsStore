package ru.mirea.autopartsstore.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long partId,
        String sku,
        String partName,

        Integer quantity,

        BigDecimal unitPrice,
        BigDecimal totalPrice

) {
}
