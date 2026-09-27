package ru.mirea.autopartsstore.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.entity.UserRole;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.InsufficientStockException;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.inventory.service.InventoryService;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderItemRequest;
import ru.mirea.autopartsstore.order.dto.OrderResponse;
import ru.mirea.autopartsstore.order.entity.CustomerOrder;
import ru.mirea.autopartsstore.order.entity.OrderItem;
import ru.mirea.autopartsstore.order.entity.OrderStatus;
import ru.mirea.autopartsstore.order.repository.OrderItemRepository;
import ru.mirea.autopartsstore.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private AppUser user;
    private Part part;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setId(10L);
        customer.setName("Ivan Ivanov");
        customer.setEmail("ivan@test.ru");

        user = new AppUser();
        user.setId(1L);
        user.setEmail("ivan@test.ru");
        user.setRole(UserRole.USER);
        user.setCustomer(customer);

        part = new Part();
        part.setId(5L);
        part.setName("Oil filter");
        part.setSku("FILTER-001");
        part.setPrice(
                new BigDecimal("500.00")
        );
    }

    @Test
    void create_shouldCreateOrder() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new OrderItemRequest(
                                        5L,
                                        2
                                )
                        )
                );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                partRepository.findById(5L)
        ).thenReturn(Optional.of(part));

        when(
                orderRepository.save(
                        any(CustomerOrder.class)
                )
        ).thenAnswer(invocation -> {

            CustomerOrder order =
                    invocation.getArgument(0);

            if (order.getId() == null) {
                order.setId(100L);
            }

            return order;
        });

        when(
                orderItemRepository.save(
                        any(OrderItem.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        OrderResponse response =
                orderService.create(
                        "ivan@test.ru",
                        request
                );

        assertNotNull(response);

        assertEquals(
                100L,
                response.id()
        );

        assertEquals(
                10L,
                response.customerId()
        );

        assertEquals(
                OrderStatus.CREATED,
                response.status()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                response.totalPrice()
        );

        assertEquals(
                1,
                response.items().size()
        );

        verify(inventoryService)
                .decreaseForOrder(
                        5L,
                        2,
                        100L
                );

        verify(orderItemRepository)
                .save(
                        any(OrderItem.class)
                );
    }

    @Test
    void create_shouldRejectDuplicateParts() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new OrderItemRequest(
                                        5L,
                                        2
                                ),
                                new OrderItemRequest(
                                        5L,
                                        3
                                )
                        )
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> orderService.create(
                                "ivan@test.ru",
                                request
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("duplicated")
        );

        verifyNoInteractions(
                appUserRepository,
                partRepository,
                inventoryService,
                orderRepository
        );
    }

    @Test
    void changeStatus_shouldConfirmCreatedOrder() {

        CustomerOrder order =
                new CustomerOrder();

        order.setId(100L);
        order.setCustomer(customer);
        order.setStatus(
                OrderStatus.CREATED
        );

        order.setTotalPrice(
                new BigDecimal("1000.00")
        );

        when(
                orderRepository.findById(100L)
        ).thenReturn(Optional.of(order));

        when(
                orderItemRepository
                        .findByOrder_Id(100L)
        ).thenReturn(List.of());

        OrderResponse response =
                orderService.changeStatus(
                        100L,
                        OrderStatus.CONFIRMED
                );

        assertEquals(
                OrderStatus.CONFIRMED,
                response.status()
        );

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        verify(orderRepository)
                .save(order);

        verify(
                inventoryService,
                never()
        ).returnForOrder(
                anyLong(),
                anyInt(),
                anyLong()
        );
    }

    @Test
    void changeStatus_shouldRejectCreatedToCompleted() {

        CustomerOrder order =
                new CustomerOrder();

        order.setId(100L);
        order.setCustomer(customer);
        order.setStatus(
                OrderStatus.CREATED
        );

        when(
                orderRepository.findById(100L)
        ).thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.changeStatus(
                        100L,
                        OrderStatus.COMPLETED
                )
        );

        assertEquals(
                OrderStatus.CREATED,
                order.getStatus()
        );

        verify(
                orderRepository,
                never()
        ).save(any());
    }

    @Test
    void changeStatus_shouldReturnItemsWhenCancelled() {

        CustomerOrder order =
                new CustomerOrder();

        order.setId(100L);
        order.setCustomer(customer);
        order.setStatus(
                OrderStatus.CONFIRMED
        );

        order.setTotalPrice(
                new BigDecimal("1000.00")
        );

        OrderItem item =
                new OrderItem();

        item.setOrder(order);
        item.setPart(part);
        item.setQuantity(2);
        item.setUnitPrice(
                new BigDecimal("500.00")
        );

        when(
                orderRepository.findById(100L)
        ).thenReturn(Optional.of(order));

        when(
                orderItemRepository
                        .findByOrder_Id(100L)
        ).thenReturn(List.of(item));

        OrderResponse response =
                orderService.changeStatus(
                        100L,
                        OrderStatus.CANCELLED
                );

        assertEquals(
                OrderStatus.CANCELLED,
                response.status()
        );

        verify(inventoryService)
                .returnForOrder(
                        5L,
                        2,
                        100L
                );
    }

    @Test
    void changeStatus_shouldRejectRepeatedCancellation() {

        CustomerOrder order =
                new CustomerOrder();

        order.setId(100L);
        order.setCustomer(customer);
        order.setStatus(
                OrderStatus.CANCELLED
        );

        when(
                orderRepository.findById(100L)
        ).thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.changeStatus(
                        100L,
                        OrderStatus.CANCELLED
                )
        );

        verifyNoInteractions(
                inventoryService
        );
    }

    @Test
    void create_shouldFailWhenPartNotFound() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new OrderItemRequest(
                                        999L,
                                        1
                                )
                        )
                );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                orderRepository.save(
                        any(CustomerOrder.class)
                )
        ).thenAnswer(invocation -> {

            CustomerOrder order =
                    invocation.getArgument(0);

            order.setId(100L);

            return order;
        });

        when(
                partRepository.findById(999L)
        ).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.create(
                        "ivan@test.ru",
                        request
                )
        );

        verify(
                inventoryService,
                never()
        ).decreaseForOrder(
                anyLong(),
                anyInt(),
                anyLong()
        );

        verify(
                orderItemRepository,
                never()
        ).save(any());
    }

    @Test
    void create_shouldFailWhenStockIsInsufficient() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new OrderItemRequest(
                                        5L,
                                        10
                                )
                        )
                );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                orderRepository.save(
                        any(CustomerOrder.class)
                )
        ).thenAnswer(invocation -> {

            CustomerOrder order =
                    invocation.getArgument(0);

            order.setId(100L);

            return order;
        });

        when(
                partRepository.findById(5L)
        ).thenReturn(Optional.of(part));

        doThrow(
                new InsufficientStockException(
                        "Not enough stock"
                )
        ).when(inventoryService)
                .decreaseForOrder(
                        5L,
                        10,
                        100L
                );

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.create(
                        "ivan@test.ru",
                        request
                )
        );

        verify(
                orderItemRepository,
                never()
        ).save(any());
    }

    @Test
    void create_shouldRejectAdminWithoutCustomer() {

        AppUser admin = new AppUser();

        admin.setId(2L);
        admin.setEmail("admin@test.ru");
        admin.setRole(UserRole.ADMIN);
        admin.setCustomer(null);

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new OrderItemRequest(
                                        5L,
                                        1
                                )
                        )
                );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "admin@test.ru"
                        )
        ).thenReturn(Optional.of(admin));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.create(
                        "admin@test.ru",
                        request
                )
        );

        verifyNoInteractions(
                partRepository,
                inventoryService,
                orderRepository,
                orderItemRepository
        );
    }

    @Test
    void findById_shouldReturnOwnOrder() {

        CustomerOrder order =
                new CustomerOrder();

        order.setId(100L);
        order.setCustomer(customer);
        order.setStatus(
                OrderStatus.CREATED
        );

        order.setTotalPrice(
                new BigDecimal("500.00")
        );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                orderRepository.findById(100L)
        ).thenReturn(Optional.of(order));

        when(
                orderItemRepository
                        .findByOrder_Id(100L)
        ).thenReturn(List.of());

        OrderResponse response =
                orderService.findById(
                        "ivan@test.ru",
                        100L
                );

        assertEquals(
                100L,
                response.id()
        );

        assertEquals(
                10L,
                response.customerId()
        );
    }

    @Test
    void findById_shouldRejectAnotherCustomersOrder() {

        Customer anotherCustomer =
                new Customer();

        anotherCustomer.setId(20L);
        anotherCustomer.setName("Petr");

        CustomerOrder order =
                new CustomerOrder();

        order.setId(200L);
        order.setCustomer(
                anotherCustomer
        );

        order.setStatus(
                OrderStatus.CREATED
        );

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                orderRepository.findById(200L)
        ).thenReturn(Optional.of(order));

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.findById(
                        "ivan@test.ru",
                        200L
                )
        );

        verify(
                orderItemRepository,
                never()
        ).findByOrder_Id(anyLong());
    }

    @Test
    void findAll_shouldIgnoreCustomerIdForUser() {

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "ivan@test.ru"
                        )
        ).thenReturn(Optional.of(user));

        when(
                orderRepository.findFiltered(
                        isNull(),
                        eq(10L),
                        eq(false),
                        any(LocalDateTime.class),
                        eq(false),
                        any(LocalDateTime.class),
                        any(Pageable.class)
                )
        ).thenReturn(Page.empty());

        orderService.findAll(
                "ivan@test.ru",

                null,

                // пользователь пытается запросить
                // чужого customer
                999L,

                null,
                null,

                0,
                20
        );

        verify(orderRepository)
                .findFiltered(
                        isNull(),

                        // Должно уйти 10, а не 999
                        eq(10L),

                        eq(false),
                        any(LocalDateTime.class),

                        eq(false),
                        any(LocalDateTime.class),

                        any(Pageable.class)
                );
    }

    @Test
    void findAll_shouldAllowCustomerFilterForAdmin() {

        AppUser admin = new AppUser();

        admin.setId(2L);
        admin.setEmail("admin@test.ru");
        admin.setRole(UserRole.ADMIN);

        when(
                appUserRepository
                        .findByEmailIgnoreCase(
                                "admin@test.ru"
                        )
        ).thenReturn(Optional.of(admin));

        when(
                orderRepository.findFiltered(
                        isNull(),
                        eq(999L),
                        eq(false),
                        any(LocalDateTime.class),
                        eq(false),
                        any(LocalDateTime.class),
                        any(Pageable.class)
                )
        ).thenReturn(Page.empty());

        orderService.findAll(
                "admin@test.ru",
                null,
                999L,
                null,
                null,
                0,
                20
        );

        verify(orderRepository)
                .findFiltered(
                        isNull(),
                        eq(999L),
                        eq(false),
                        any(LocalDateTime.class),
                        eq(false),
                        any(LocalDateTime.class),
                        any(Pageable.class)
                );
    }


}