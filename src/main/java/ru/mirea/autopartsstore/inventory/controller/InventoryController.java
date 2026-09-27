package ru.mirea.autopartsstore.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Inventory",
        description = "Управление складскими остатками"
)
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @Operation(
            summary = "Получить складские остатки",
            description = "Возвращает остатки запчастей на складе"
    )
    @GetMapping
    public List<StockResponse> findAllStock(
            @RequestParam(required = false)
            Integer maxQuantity
    ) {
        return inventoryService.findAllStock(maxQuantity);
    }

    @Operation(
            summary = "Оприходовать запчасть",
            description = "Увеличивает остаток и создаёт складское движение RECEIPT"
    )
    @PostMapping("/parts/{partId}/receipt")
    public StockResponse receipt(
            @PathVariable Long partId,
            @Valid @RequestBody InventoryOperationRequest request
    ) {
        return inventoryService.receipt(partId, request);
    }

    @Operation(summary = "Получить остаток запчасти")
    @GetMapping("/parts/{partId}")
    public StockResponse getStock(
            @PathVariable Long partId
    ) {
        return inventoryService.getStock(partId);
    }

    @Operation(
            summary = "Списать запчасть",
            description = "Уменьшает остаток и создаёт складское движение WRITE_OFF"
    )
    @PostMapping("/parts/{partId}/write-off")
    public StockResponse writeOff(
            @PathVariable Long partId,
            @Valid @RequestBody InventoryOperationRequest request
    ) {
        return inventoryService.writeOff(partId, request);
    }

    @Operation(
            summary = "Получить движения запчасти",
            description = "Возвращает историю складских операций для указанной запчасти"
    )
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

    @Operation(
            summary = "Получить движения по складу",
            description = "Возвращает историю складских операций с пагинацией"
    )
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