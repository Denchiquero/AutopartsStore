package ru.mirea.autopartsstore.inventory.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.inventory.dto.InventoryOperationRequest;
import ru.mirea.autopartsstore.inventory.dto.StockResponse;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;
import ru.mirea.autopartsstore.inventory.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/parts/{partId}/receipt")
    public StockResponse receipt(
            @PathVariable Long partId,
            @Valid @RequestBody InventoryOperationRequest request
    ) {
        return inventoryService.receipt(partId, request);
    }

    @GetMapping("/parts/{partId}")
    public StockResponse getStock(
            @PathVariable Long partId
    ) {
        return inventoryService.getStock(partId);
    }


    @PostMapping("/parts/{partId}/write-off")
    public StockResponse writeOff(
            @PathVariable Long partId,
            @Valid @RequestBody InventoryOperationRequest request
    ) {
        return inventoryService.writeOff(partId, request);
    }


    @GetMapping("/parts/{partId}/movements")
    public List<InventoryMovement> getMovements(
            @PathVariable Long partId
    ) {
        return inventoryService.getMovements(partId);
    }
}