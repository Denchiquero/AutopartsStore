package ru.mirea.autopartsstore.auth.dto;

import ru.mirea.autopartsstore.auth.entity.UserRole;

public record CurrentUserResponse(
        Long userId,
        String email,
        UserRole role,
        Long customerId
) {
}