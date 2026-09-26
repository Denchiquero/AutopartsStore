package ru.mirea.autopartsstore.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 30)
        String phone,

        @NotBlank
        @Email
        @Size(max = 255)
        String email

) {
}