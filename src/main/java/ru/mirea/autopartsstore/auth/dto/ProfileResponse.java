package ru.mirea.autopartsstore.auth.dto;

import ru.mirea.autopartsstore.auth.entity.UserRole;

public record ProfileResponse(
        Long userId,
        String email,
        UserRole role,

        Long customerId,
        String name,
        String phone
) {
}