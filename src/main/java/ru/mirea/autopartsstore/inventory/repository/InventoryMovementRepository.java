package ru.mirea.autopartsstore.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;

public interface InventoryMovementRepository
        extends JpaRepository<InventoryMovement, Long> {

    Page<InventoryMovement> findByPart_Id(
            Long partId,
            Pageable pageable
    );
}