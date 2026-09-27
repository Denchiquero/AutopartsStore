package ru.mirea.autopartsstore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(max = 150)
        @Schema(example = "Иван Иванов")
        String name,

        @Size(max = 30)
        @Schema(example = "+79991234567")
        String phone,

        @NotBlank
        @Email
        @Size(max = 255)
        @Schema(example = "ivan@example.com")
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        @Schema(example = "password123")
        String password
) {
}