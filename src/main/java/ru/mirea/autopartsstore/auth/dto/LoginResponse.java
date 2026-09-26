package ru.mirea.autopartsstore.auth.dto;

import ru.mirea.autopartsstore.auth.entity.UserRole;

public record LoginResponse(
        Long userId,
        String email,
        UserRole role,
        Long customerId,
        String token
) {
}