package ru.mirea.autopartsstore.auth.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.auth.dto.ProfileResponse;
import ru.mirea.autopartsstore.auth.dto.UpdateProfileRequest;
import ru.mirea.autopartsstore.auth.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(
            Authentication authentication
    ) {

        return profileService.getProfile(
                authentication.getName()
        );
    }

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