package ru.mirea.autopartsstore.inventory.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.inventory.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository
        extends JpaRepository<Stock, Long> {

    List<Stock> findByPartIdIn(List<Long> partIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM Stock s
        WHERE s.partId = :partId
        """)
    Optional<Stock> findByPartIdForUpdate(
            @Param("partId") Long partId
    );
}