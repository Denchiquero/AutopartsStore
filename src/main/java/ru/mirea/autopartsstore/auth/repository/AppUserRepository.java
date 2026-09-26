package ru.mirea.autopartsstore.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.auth.entity.AppUser;

import java.util.Optional;

public interface AppUserRepository
        extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );
}