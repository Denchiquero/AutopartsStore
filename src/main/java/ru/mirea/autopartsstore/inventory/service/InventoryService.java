package ru.mirea.autopartsstore.inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.inventory.dto.InventoryOperationRequest;
import ru.mirea.autopartsstore.inventory.dto.StockResponse;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;
import ru.mirea.autopartsstore.inventory.entity.MovementType;
import ru.mirea.autopartsstore.inventory.entity.Stock;
import ru.mirea.autopartsstore.inventory.repository.InventoryMovementRepository;
import ru.mirea.autopartsstore.inventory.repository.StockRepository;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    private final StockRepository stockRepository;
    private final InventoryMovementRepository movementRepository;
    private final PartRepository partRepository;

    public InventoryService(
            StockRepository stockRepository,
            InventoryMovementRepository movementRepository,
            PartRepository partRepository
    ) {
        this.stockRepository = stockRepository;
        this.movementRepository = movementRepository;
        this.partRepository = partRepository;
    }

    @Transactional
    public StockResponse receipt(
            Long partId,
            InventoryOperationRequest request
    ) {

        Part part = partRepository.findById(partId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Part with id " + partId + " not found"
                        )
                );

        Stock stock = stockRepository.findById(partId)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setPart(part);
                    newStock.setQuantity(0);
                    return newStock;
                });

        stock.setQuantity(
                stock.getQuantity() + request.quantity()
        );

        stockRepository.save(stock);

        InventoryMovement movement = new InventoryMovement();

        movement.setPart(part);
        movement.setType(MovementType.RECEIPT);
        movement.setQuantity(request.quantity());
        movement.setCreatedAt(LocalDateTime.now());
        movement.setComment(request.comment());

        movementRepository.save(movement);

        return toResponse(stock);
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getPart().getId(),
                stock.getPart().getSku(),
                stock.getPart().getName(),
                stock.getQuantity()
        );
    }
}