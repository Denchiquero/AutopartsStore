package ru.mirea.autopartsstore.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.autopartsstore.auth.dto.*;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.entity.UserRole;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(
            RegisterRequest request
    ) {

        String email = request.email()
                .trim()
                .toLowerCase();

        if (appUserRepository
                .existsByEmailIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        if (customerRepository
                .existsByEmailIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "Customer with this email already exists"
            );
        }

        Customer customer = new Customer();

        customer.setName(request.name().trim());
        customer.setPhone(request.phone());
        customer.setEmail(email);

        customer = customerRepository.save(customer);

        AppUser user = new AppUser();

        user.setEmail(email);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setRole(UserRole.USER);
        user.setCustomer(customer);

        user = appUserRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                customer.getId(),
                customer.getName()
        );
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        AppUser user = appUserRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(user);

        Long customerId = null;

        if (user.getCustomer() != null) {
            customerId = user.getCustomer().getId();
        }

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                customerId,
                token
        );
    }

    public CurrentUserResponse getCurrentUser(
            String email
    ) {

        AppUser user =
                appUserRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        Long customerId = null;

        if (user.getCustomer() != null) {
            customerId =
                    user.getCustomer().getId();
        }

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                customerId
        );
    }
}