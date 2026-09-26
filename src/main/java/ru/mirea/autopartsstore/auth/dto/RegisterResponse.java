package ru.mirea.autopartsstore.auth.dto;

import ru.mirea.autopartsstore.auth.entity.UserRole;

public record RegisterResponse(
        Long userId,
        String email,
        UserRole role,
        Long customerId,
        String customerName
) {
}