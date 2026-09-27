package ru.mirea.autopartsstore.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.common.exception.InsufficientStockException;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.inventory.dto.InventoryMovementResponse;
import ru.mirea.autopartsstore.inventory.dto.InventoryOperationRequest;
import ru.mirea.autopartsstore.inventory.dto.StockResponse;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;
import ru.mirea.autopartsstore.inventory.entity.MovementType;
import ru.mirea.autopartsstore.inventory.entity.Stock;
import ru.mirea.autopartsstore.inventory.repository.InventoryMovementRepository;
import ru.mirea.autopartsstore.inventory.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Stock stock = stockRepository.findByPartIdForUpdate(partId)
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

        Part part = stock.getPart();

        return new StockResponse(
                part.getId(),
                part.getSku(),
                part.getName(),
                part.getManufacturer().getName(),
                part.getCategory().getName(),
                part.getPrice(),
                stock.getQuantity()
        );
    }

    public StockResponse getStock(Long partId) {

        Stock stock = stockRepository.findById(partId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock for part with id " + partId + " not found"
                        )
                );

        return toResponse(stock);
    }

    @Transactional
    public StockResponse writeOff(
            Long partId,
            InventoryOperationRequest request
    ) {

        Stock stock = stockRepository.findByPartIdForUpdate(partId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock for part with id " + partId + " not found"
                        )
                );

        if (stock.getQuantity() < request.quantity()) {
            throw new InsufficientStockException(
                    "Not enough stock. Available: " + stock.getQuantity()
            );
        }

        stock.setQuantity(
                stock.getQuantity() - request.quantity()
        );

        stockRepository.save(stock);

        InventoryMovement movement = new InventoryMovement();

        movement.setPart(stock.getPart());
        movement.setType(MovementType.WRITE_OFF);
        movement.setQuantity(request.quantity());
        movement.setCreatedAt(LocalDateTime.now());
        movement.setComment(request.comment());

        movementRepository.save(movement);

        return toResponse(stock);
    }

    public PageResponse<InventoryMovementResponse> getMovements(
            Long partId,
            int page,
            int size
    ) {

        if (!partRepository.existsById(partId)) {
            throw new ResourceNotFoundException(
                    "Part with id " + partId + " not found"
            );
        }

        validatePage(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<InventoryMovement> result =
                movementRepository.findByPart_Id(
                        partId,
                        pageable
                );

        List<InventoryMovementResponse> content =
                result.getContent()
                        .stream()
                        .map(this::toMovementResponse)
                        .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Transactional
    public void decreaseForOrder(
            Long partId,
            Integer quantity,
            Long orderId
    ) {

        Stock stock = stockRepository.findByPartIdForUpdate(partId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Part with id " + partId + " is out of stock"
                        )
                );

        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Not enough stock for part "
                            + partId
                            + ". Requested: "
                            + quantity
                            + ", available: "
                            + stock.getQuantity()
            );
        }

        stock.setQuantity(
                stock.getQuantity() - quantity
        );

        stockRepository.save(stock);

        InventoryMovement movement =
                new InventoryMovement();

        movement.setPart(stock.getPart());
        movement.setType(MovementType.ORDER);
        movement.setQuantity(quantity);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setOrderId(orderId);
        movement.setComment(
                "Write-off for order #" + orderId
        );

        movementRepository.save(movement);
    }

    @Transactional
    public void returnForOrder(
            Long partId,
            Integer quantity,
            Long orderId
    ) {

        Stock stock = stockRepository.findByPartIdForUpdate(partId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Stock for part with id " + partId + " not found"
                        )
                );

        stock.setQuantity(
                stock.getQuantity() + quantity
        );

        stockRepository.save(stock);

        InventoryMovement movement = new InventoryMovement();

        movement.setPart(stock.getPart());
        movement.setType(MovementType.RETURN);
        movement.setQuantity(quantity);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setOrderId(orderId);
        movement.setComment(
                "Return from cancelled order #" + orderId
        );

        movementRepository.save(movement);
    }

    public List<StockResponse> findAllStock(
            Integer maxQuantity
    ) {

        Map<Long, Integer> stockMap =
                stockRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                Stock::getPartId,
                                Stock::getQuantity
                        ));

        return partRepository.findAll()
                .stream()
                .map(part -> {

                    Integer quantity =
                            stockMap.getOrDefault(
                                    part.getId(),
                                    0
                            );

                    return new StockResponse(
                            part.getId(),
                            part.getSku(),
                            part.getName(),

                            part.getManufacturer().getName(),
                            part.getCategory().getName(),

                            part.getPrice(),
                            quantity
                    );
                })
                .filter(stock ->
                        maxQuantity == null
                                || stock.quantity() <= maxQuantity
                )
                .toList();
    }

    private InventoryMovementResponse toMovementResponse(
            InventoryMovement movement
    ) {

        Part part = movement.getPart();

        return new InventoryMovementResponse(
                movement.getId(),

                part.getId(),
                part.getSku(),
                part.getName(),

                movement.getType(),
                movement.getQuantity(),

                movement.getCreatedAt(),

                movement.getOrderId(),
                movement.getComment()
        );
    }

    public PageResponse<InventoryMovementResponse> findAllMovements(
            int page,
            int size
    ) {

        validatePage(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<InventoryMovement> result =
                movementRepository.findAll(pageable);

        List<InventoryMovementResponse> content =
                result.getContent()
                        .stream()
                        .map(this::toMovementResponse)
                        .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    private void validatePage(
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }


}