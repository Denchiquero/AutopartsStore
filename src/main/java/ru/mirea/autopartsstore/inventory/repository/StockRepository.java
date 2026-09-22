package ru.mirea.autopartsstore.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.inventory.entity.Stock;

public interface StockRepository
        extends JpaRepository<Stock, Long> {
}