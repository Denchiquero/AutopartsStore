package ru.mirea.autopartsstore.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;

import java.util.List;

public interface InventoryMovementRepository
        extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement>
    findByPart_IdOrderByCreatedAtDesc(Long partId);
}