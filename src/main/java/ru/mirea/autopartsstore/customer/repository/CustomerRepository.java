package ru.mirea.autopartsstore.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.customer.entity.Customer;

import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}