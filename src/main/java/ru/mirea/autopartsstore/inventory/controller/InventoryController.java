package ru.mirea.autopartsstore.inventory.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.inventory.dto.InventoryMovementResponse;
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
    public PageResponse<InventoryMovementResponse> getMovements(

            @PathVariable Long partId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ) {
        return inventoryService.getMovements(
                partId,
                page,
                size
        );
    }

    @GetMapping
    public List<StockResponse> findAllStock(
            @RequestParam(required = false)
            Integer maxQuantity
    ) {
        return inventoryService.findAllStock(maxQuantity);
    }

    @GetMapping("/movements")
    public PageResponse<InventoryMovementResponse> findAllMovements(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ) {
        return inventoryService.findAllMovements(
                page,
                size
        );
    }
}