package ru.mirea.autopartsstore.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.InsufficientStockException;
import ru.mirea.autopartsstore.inventory.dto.InventoryOperationRequest;
import ru.mirea.autopartsstore.inventory.entity.InventoryMovement;
import ru.mirea.autopartsstore.inventory.entity.MovementType;
import ru.mirea.autopartsstore.inventory.entity.Stock;
import ru.mirea.autopartsstore.inventory.repository.InventoryMovementRepository;
import ru.mirea.autopartsstore.inventory.repository.StockRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Part part;

    @BeforeEach
    void setUp() {

        Manufacturer manufacturer =
                new Manufacturer();

        manufacturer.setId(1L);
        manufacturer.setName("MANN-FILTER");

        PartCategory category =
                new PartCategory();

        category.setId(1L);
        category.setName("Oil filters");

        part = new Part();

        part.setId(5L);
        part.setName("Oil filter");
        part.setSku("FILTER-001");
        part.setArticle("W68/3");
        part.setPrice(
                new BigDecimal("500.00")
        );

        part.setManufacturer(manufacturer);
        part.setCategory(category);
    }

    private Stock createStock(int quantity) {

        Stock stock = new Stock();

        stock.setPartId(5L);
        stock.setPart(part);
        stock.setQuantity(quantity);

        return stock;
    }

    @Test
    void receipt_shouldCreateStockWhenStockDoesNotExist() {

        InventoryOperationRequest request =
                new InventoryOperationRequest(
                        10,
                        "First receipt"
                );

        when(partRepository.findById(5L))
                .thenReturn(Optional.of(part));

        when(stockRepository.findByPartIdForUpdate(5L))
                .thenReturn(Optional.empty());

        when(stockRepository.save(any(Stock.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        inventoryService.receipt(
                5L,
                request
        );

        ArgumentCaptor<Stock> stockCaptor =
                ArgumentCaptor.forClass(Stock.class);

        verify(stockRepository)
                .save(stockCaptor.capture());

        Stock savedStock =
                stockCaptor.getValue();

        assertEquals(10, savedStock.getQuantity());
        assertEquals(5L, savedStock.getPart().getId());

        ArgumentCaptor<InventoryMovement> movementCaptor =
                ArgumentCaptor.forClass(
                        InventoryMovement.class
                );

        verify(movementRepository)
                .save(movementCaptor.capture());

        InventoryMovement movement =
                movementCaptor.getValue();

        assertEquals(
                MovementType.RECEIPT,
                movement.getType()
        );

        assertEquals(
                10,
                movement.getQuantity()
        );
    }

    @Test
    void writeOff_shouldDecreaseStock() {

        Stock stock =
                createStock(10);

        when(
                stockRepository
                        .findByPartIdForUpdate(5L)
        ).thenReturn(Optional.of(stock));

        InventoryOperationRequest request =
                new InventoryOperationRequest(
                        4,
                        "Damaged"
                );

        inventoryService.writeOff(
                5L,
                request
        );

        assertEquals(
                6,
                stock.getQuantity()
        );

        ArgumentCaptor<InventoryMovement> captor =
                ArgumentCaptor.forClass(
                        InventoryMovement.class
                );

        verify(movementRepository)
                .save(captor.capture());

        InventoryMovement movement =
                captor.getValue();

        assertEquals(
                MovementType.WRITE_OFF,
                movement.getType()
        );

        assertEquals(
                4,
                movement.getQuantity()
        );
    }

    @Test
    void writeOff_shouldRejectInsufficientStock() {

        Stock stock =
                createStock(3);

        when(
                stockRepository
                        .findByPartIdForUpdate(5L)
        ).thenReturn(Optional.of(stock));

        InventoryOperationRequest request =
                new InventoryOperationRequest(
                        10,
                        "Write-off"
                );

        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.writeOff(
                        5L,
                        request
                )
        );

        assertEquals(
                3,
                stock.getQuantity()
        );

        verify(
                movementRepository,
                never()
        ).save(any());
    }

    @Test
    void decreaseForOrder_shouldDecreaseStockAndCreateMovement() {

        Stock stock =
                createStock(10);

        when(
                stockRepository
                        .findByPartIdForUpdate(5L)
        ).thenReturn(Optional.of(stock));

        inventoryService.decreaseForOrder(
                5L,
                3,
                100L
        );

        assertEquals(
                7,
                stock.getQuantity()
        );

        ArgumentCaptor<InventoryMovement> captor =
                ArgumentCaptor.forClass(
                        InventoryMovement.class
                );

        verify(movementRepository)
                .save(captor.capture());

        InventoryMovement movement =
                captor.getValue();

        assertEquals(
                MovementType.ORDER,
                movement.getType()
        );

        assertEquals(
                3,
                movement.getQuantity()
        );

        assertEquals(
                100L,
                movement.getOrderId()
        );
    }

    @Test
    void returnForOrder_shouldIncreaseStockAndCreateMovement() {

        Stock stock =
                createStock(7);

        when(stockRepository.findByPartIdForUpdate(5L))
                .thenReturn(Optional.of(stock));

        inventoryService.returnForOrder(
                5L,
                3,
                100L
        );

        assertEquals(
                10,
                stock.getQuantity()
        );

        ArgumentCaptor<InventoryMovement> captor =
                ArgumentCaptor.forClass(
                        InventoryMovement.class
                );

        verify(movementRepository)
                .save(captor.capture());

        InventoryMovement movement =
                captor.getValue();

        assertEquals(
                MovementType.RETURN,
                movement.getType()
        );

        assertEquals(
                3,
                movement.getQuantity()
        );

        assertEquals(
                100L,
                movement.getOrderId()
        );
    }
}