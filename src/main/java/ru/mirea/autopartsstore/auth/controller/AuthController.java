package ru.mirea.autopartsstore.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.auth.dto.*;
import ru.mirea.autopartsstore.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentification",
        description = "Аутентификация пользователей"
)

public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @Operation(summary = "Зарегистрировать пользователя")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {
        return authService.register(request);
    }

    @Operation(summary = "Войти в систему")
    @PostMapping("/login")
    public LoginResponse login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {
        return authService.login(request);
    }

    @Operation(summary = "Получить текущего пользователя")
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public CurrentUserResponse me(
            Authentication authentication
    ) {

        return authService.getCurrentUser(
                authentication.getName()
        );
    }
}