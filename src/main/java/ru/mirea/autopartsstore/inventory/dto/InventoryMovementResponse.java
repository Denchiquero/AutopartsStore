package ru.mirea.autopartsstore.inventory.dto;

import ru.mirea.autopartsstore.inventory.entity.MovementType;

import java.time.LocalDateTime;

public record InventoryMovementResponse(

        Long id,

        Long partId,
        String sku,
        String partName,

        MovementType type,
        Integer quantity,

        LocalDateTime createdAt,

        Long orderId,
        String comment

) {
}