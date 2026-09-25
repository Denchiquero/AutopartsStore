package ru.mirea.autopartsstore.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.inventory.entity.Stock;

import java.util.List;

public interface StockRepository
        extends JpaRepository<Stock, Long> {

    List<Stock> findByPartIdIn(List<Long> partIds);
}