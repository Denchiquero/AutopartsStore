package ru.mirea.autopartsstore.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.autopartsstore.auth.dto.ProfileResponse;
import ru.mirea.autopartsstore.auth.dto.UpdateProfileRequest;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;

@Service
public class ProfileService {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;

    public ProfileService(
            AppUserRepository appUserRepository,
            CustomerRepository customerRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
    }

    public ProfileResponse getProfile(
            String email
    ) {

        AppUser user = getUser(email);

        Customer customer = user.getCustomer();

        if (customer == null) {
            return new ProfileResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    null,
                    null,
                    null
            );
        }

        return toResponse(
                user,
                customer
        );
    }

    @Transactional
    public ProfileResponse updateProfile(
            String currentEmail,
            UpdateProfileRequest request
    ) {

        AppUser user =
                getUser(currentEmail);

        Customer customer =
                user.getCustomer();

        if (customer == null) {
            throw new IllegalStateException(
                    "Customer profile not found"
            );
        }

        String newEmail =
                request.email()
                        .trim()
                        .toLowerCase();

        if (appUserRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        newEmail,
                        user.getId()
                )) {

            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        if (customerRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        newEmail,
                        customer.getId()
                )) {

            throw new IllegalArgumentException(
                    "Customer with this email already exists"
            );
        }

        customer.setName(
                request.name().trim()
        );

        customer.setPhone(
                request.phone()
        );

        customer.setEmail(
                newEmail
        );

        user.setEmail(
                newEmail
        );

        customerRepository.save(customer);
        appUserRepository.save(user);

        return toResponse(
                user,
                customer
        );
    }

    private AppUser getUser(
            String email
    ) {

        return appUserRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private ProfileResponse toResponse(
            AppUser user,
            Customer customer
    ) {

        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),

                customer.getId(),
                customer.getName(),
                customer.getPhone()
        );
    }
}