package ru.mirea.autopartsstore.customer.dto;

public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email
) {
}