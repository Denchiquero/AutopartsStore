package ru.mirea.autopartsstore.catalog.dto;

public record ManufacturerResponse(
        Long id,
        String name,
        String country,
        String website
) {
}