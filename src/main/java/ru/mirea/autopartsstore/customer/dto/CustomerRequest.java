package ru.mirea.autopartsstore.customer.dto;

import jakarta.validation.constraints.*;

public record CustomerRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 30)
        String phone,

        @Email
        @Size(max = 255)
        String email

) {
}