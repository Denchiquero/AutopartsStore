package ru.mirea.autopartsstore.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.auth.dto.ProfileResponse;
import ru.mirea.autopartsstore.auth.dto.UpdateProfileRequest;
import ru.mirea.autopartsstore.auth.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@Tag(
        name = "Profile",
        description = "Профиль пользователя"
)
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @Operation(summary = "Получить профиль")
    @GetMapping
    public ProfileResponse getProfile(
            Authentication authentication
    ) {

        return profileService.getProfile(
                authentication.getName()
        );
    }

    @Operation(summary = "Изменить профиль")
    @PutMapping
    public ProfileResponse updateProfile(
            Authentication authentication,
            @Valid
            @RequestBody
            UpdateProfileRequest request
    ) {

        return profileService.updateProfile(
                authentication.getName(),
                request
        );
    }
}