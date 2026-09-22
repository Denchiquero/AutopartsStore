package ru.mirea.autopartsstore.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.order.entity.CustomerOrder;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {
}