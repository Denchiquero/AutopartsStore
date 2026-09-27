package ru.mirea.autopartsstore.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.entity.UserRole;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;
import ru.mirea.autopartsstore.catalog.repository.ManufacturerRepository;
import ru.mirea.autopartsstore.catalog.repository.PartCategoryRepository;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.InsufficientStockException;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;
import ru.mirea.autopartsstore.inventory.entity.Stock;
import ru.mirea.autopartsstore.inventory.repository.InventoryMovementRepository;
import ru.mirea.autopartsstore.inventory.repository.StockRepository;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderItemRequest;
import ru.mirea.autopartsstore.order.repository.OrderItemRepository;
import ru.mirea.autopartsstore.order.repository.OrderRepository;
import ru.mirea.autopartsstore.order.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class OrderTransactionIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    "postgres:17-alpine"
            );

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private PartCategoryRepository categoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private Part firstPart;
    private Part secondPart;

    @BeforeEach
    void setUp() {

        Manufacturer manufacturer =
                new Manufacturer();

        manufacturer.setName("TEST-MANUFACTURER");
        manufacturer.setCountry("Germany");

        manufacturer =
                manufacturerRepository.save(manufacturer);


        PartCategory category =
                new PartCategory();

        category.setName("Test category");
        category.setDescription(
                "Category for integration tests"
        );

        category =
                categoryRepository.save(category);


        firstPart = new Part();

        firstPart.setName("First test part");
        firstPart.setSku("TEST-PART-001");
        firstPart.setArticle("TEST-001");
        firstPart.setDescription("Test");
        firstPart.setPrice(
                new BigDecimal("500.00")
        );
        firstPart.setManufacturer(manufacturer);
        firstPart.setCategory(category);

        firstPart =
                partRepository.save(firstPart);


        secondPart = new Part();

        secondPart.setName("Second test part");
        secondPart.setSku("TEST-PART-002");
        secondPart.setArticle("TEST-002");
        secondPart.setDescription("Test");
        secondPart.setPrice(
                new BigDecimal("1000.00")
        );
        secondPart.setManufacturer(manufacturer);
        secondPart.setCategory(category);

        secondPart =
                partRepository.save(secondPart);


        Stock firstStock = new Stock();

        firstStock.setPart(firstPart);
        firstStock.setQuantity(10);

        stockRepository.save(firstStock);


        Stock secondStock = new Stock();

        secondStock.setPart(secondPart);
        secondStock.setQuantity(1);

        stockRepository.save(secondStock);


        Customer customer =
                new Customer();

        customer.setName("Integration User");
        customer.setPhone("+79999999999");
        customer.setEmail(
                "integration@test.ru"
        );

        customer =
                customerRepository.save(customer);


        AppUser user =
                new AppUser();

        user.setEmail(
                "integration@test.ru"
        );

        // В этом тесте пароль вообще не используется
        user.setPasswordHash("test");
        user.setRole(UserRole.USER);
        user.setCustomer(customer);

        appUserRepository.save(user);
    }

    @Test
    void create_shouldRollbackEverythingWhenSecondPartIsOutOfStock() {

        long ordersBefore =
                orderRepository.count();

        long itemsBefore =
                orderItemRepository.count();

        long movementsBefore =
                movementRepository.count();

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(

                                new OrderItemRequest(
                                        firstPart.getId(),
                                        2
                                ),

                                new OrderItemRequest(
                                        secondPart.getId(),
                                        5
                                )
                        )
                );

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.create(
                        "integration@test.ru",
                        request
                )
        );

        // Заказ должен полностью откатиться
        assertEquals(
                ordersBefore,
                orderRepository.count()
        );

        // Даже первая успевшая создаться позиция
        // должна откатиться
        assertEquals(
                itemsBefore,
                orderItemRepository.count()
        );

        // Движение склада первой детали
        // тоже должно откатиться
        assertEquals(
                movementsBefore,
                movementRepository.count()
        );

        Stock firstStock =
                stockRepository
                        .findById(firstPart.getId())
                        .orElseThrow();

        Stock secondStock =
                stockRepository
                        .findById(secondPart.getId())
                        .orElseThrow();

        // Было 10.
        // Первая операция успела списать 2,
        // но транзакция должна вернуть всё обратно.
        assertEquals(
                10,
                firstStock.getQuantity()
        );

        // Здесь списания вообще не произошло
        assertEquals(
                1,
                secondStock.getQuantity()
        );
    }
}