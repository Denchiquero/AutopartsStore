package ru.mirea.autopartsstore.order.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.entity.UserRole;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.common.exception.InvalidOrderStateException;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;
import ru.mirea.autopartsstore.inventory.service.InventoryService;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderItemRequest;
import ru.mirea.autopartsstore.order.dto.OrderItemResponse;
import ru.mirea.autopartsstore.order.dto.OrderResponse;
import ru.mirea.autopartsstore.order.entity.CustomerOrder;
import ru.mirea.autopartsstore.order.entity.OrderItem;
import ru.mirea.autopartsstore.order.entity.OrderStatus;
import ru.mirea.autopartsstore.order.repository.OrderItemRepository;
import ru.mirea.autopartsstore.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final PartRepository partRepository;
    private final InventoryService inventoryService;
    private final AppUserRepository appUserRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            PartRepository partRepository,
            InventoryService inventoryService,
            AppUserRepository appUserRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.partRepository = partRepository;
        this.inventoryService = inventoryService;
        this.appUserRepository = appUserRepository;
    }

    private OrderResponse toResponse(
            CustomerOrder order,
            List<OrderItem> orderItems
    ) {

        List<OrderItemResponse> items =
                orderItems.stream()
                        .map(item -> {

                            BigDecimal itemTotal =
                                    item.getUnitPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            item.getQuantity()
                                                    )
                                            );

                            return new OrderItemResponse(
                                    item.getPart().getId(),
                                    item.getPart().getSku(),
                                    item.getPart().getName(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    itemTotal
                            );
                        })
                        .toList();

        return new OrderResponse(
                order.getId(),

                order.getCustomer().getId(),
                order.getCustomer().getName(),

                order.getCreatedAt(),
                order.getStatus(),

                order.getTotalPrice(),

                items
        );
    }

    @Transactional
    public OrderResponse create(
            String email,
            CreateOrderRequest request
    ) {

        validateOrderItems(request.items());

        AppUser user = getCurrentUser(email);

        if (user.getCustomer() == null) {
            throw new IllegalStateException(
                    "Only customer accounts can create orders"
            );
        }

        Customer customer = user.getCustomer();

        CustomerOrder order = new CustomerOrder();

        order.setCustomer(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;

        List<OrderItem> savedItems =
                new ArrayList<>();

        for (OrderItemRequest itemRequest
                : request.items()) {

            Part part = partRepository
                    .findById(itemRequest.partId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Part with id "
                                            + itemRequest.partId()
                                            + " not found"
                            )
                    );

            inventoryService.decreaseForOrder(
                    part.getId(),
                    itemRequest.quantity(),
                    order.getId()
            );

            OrderItem item = new OrderItem();

            item.setOrder(order);
            item.setPart(part);
            item.setQuantity(
                    itemRequest.quantity()
            );
            item.setUnitPrice(
                    part.getPrice()
            );

            OrderItem savedItem =
                    orderItemRepository.save(item);

            savedItems.add(savedItem);

            BigDecimal itemTotal =
                    part.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.quantity()
                                    )
                            );

            totalPrice =
                    totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return toResponse(
                order,
                savedItems
        );
    }


    public OrderResponse findById(
            String email,
            Long id
    ) {

        AppUser user =
                getCurrentUser(email);

        CustomerOrder order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order with id "
                                                + id
                                                + " not found"
                                )
                        );

        if (user.getRole() != UserRole.ADMIN) {

            if (user.getCustomer() == null
                    || !order.getCustomer()
                    .getId()
                    .equals(
                            user.getCustomer().getId()
                    )) {

                throw new ResourceNotFoundException(
                        "Order with id "
                                + id
                                + " not found"
                );
            }
        }

        List<OrderItem> items =
                orderItemRepository
                        .findByOrder_Id(id);

        return toResponse(
                order,
                items
        );
    }

    public PageResponse<OrderResponse> findAll(
            String email,
            OrderStatus status,
            Long customerId,
            LocalDate from,
            LocalDate to,
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

        AppUser user =
                getCurrentUser(email);

        Long effectiveCustomerId;

        if (user.getRole() == UserRole.ADMIN) {

            effectiveCustomerId = customerId;

        } else {

            if (user.getCustomer() == null) {
                throw new IllegalStateException(
                        "Customer account not found"
                );
            }

            effectiveCustomerId =
                    user.getCustomer().getId();
        }

        // Нужно ли вообще применять фильтры по датам
        boolean useFrom = from != null;
        boolean useTo = to != null;

        // Даже если фильтр выключен, передаем настоящий LocalDateTime,
        // чтобы PostgreSQL мог определить тип параметра
        LocalDateTime fromDate = useFrom
                ? from.atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime toDate = useTo
                ? to.plusDays(1).atStartOfDay()
                : LocalDateTime.of(9999, 1, 1, 0, 0);

        // Заказы по умолчанию показываем от новых к старым
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        // Получаем только нужную страницу заказов
        Page<CustomerOrder> result =
                orderRepository.findFiltered(
                        status,
                        effectiveCustomerId,
                        useFrom,
                        fromDate,
                        useTo,
                        toDate,
                        pageable
                );

        List<CustomerOrder> orders =
                result.getContent();

        // Если страница пустая, к order_item вообще не обращаемся
        if (orders.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    result.isFirst(),
                    result.isLast()
            );
        }

        // Собираем ID всех заказов текущей страницы
        List<Long> orderIds = orders.stream()
                .map(CustomerOrder::getId)
                .toList();

        // Одним запросом получаем позиции сразу всех заказов
        List<OrderItem> allItems =
                orderItemRepository.findAllByOrderIds(
                        orderIds
                );

        // Группируем позиции по заказу
        Map<Long, List<OrderItem>> itemsByOrder =
                allItems.stream()
                        .collect(
                                Collectors.groupingBy(
                                        item ->
                                                item.getOrder().getId()
                                )
                        );

        // Формируем DTO
        List<OrderResponse> content =
                orders.stream()
                        .map(order ->
                                toResponse(
                                        order,
                                        itemsByOrder.getOrDefault(
                                                order.getId(),
                                                List.of()
                                        )
                                )
                        )
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


    private void returnItemsToStock(CustomerOrder order) {

        List<OrderItem> items =
                orderItemRepository.findByOrder_Id(order.getId());

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "Order #" + order.getId() + " has no items"
            );
        }

        for (OrderItem item : items) {

            inventoryService.returnForOrder(
                    item.getPart().getId(),
                    item.getQuantity(),
                    order.getId()
            );
        }
    }

    @Transactional
    public OrderResponse changeStatus(
            Long orderId,
            OrderStatus newStatus
    ) {

        CustomerOrder order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order with id "
                                                + orderId
                                                + " not found"
                                )
                        );

        validateStatusTransition(
                order.getStatus(),
                newStatus
        );

        if (newStatus == OrderStatus.CANCELLED) {
            returnItemsToStock(order);
        }

        order.setStatus(newStatus);

        orderRepository.save(order);

        List<OrderItem> items =
                orderItemRepository
                        .findByOrder_Id(orderId);

        return toResponse(
                order,
                items
        );
    }

    private AppUser getCurrentUser(String email) {

        return appUserRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private void validateOrderItems(
            List<OrderItemRequest> items
    ) {

        Set<Long> partIds = new HashSet<>();

        for (OrderItemRequest item : items) {

            if (!partIds.add(item.partId())) {
                throw new IllegalArgumentException(
                        "Part with id "
                                + item.partId()
                                + " is duplicated in order"
                );
            }
        }
    }

    private void validateStatusTransition(
            OrderStatus current,
            OrderStatus target
    ) {

        if (current == target) {
            throw new IllegalStateException(
                    "Order already has status " + current
            );
        }

        boolean allowed = switch (current) {

            case CREATED ->
                    target == OrderStatus.CONFIRMED
                            || target == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    target == OrderStatus.COMPLETED
                            || target == OrderStatus.CANCELLED;

            case CANCELLED, COMPLETED ->
                    false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Cannot change order status from "
                            + current
                            + " to "
                            + target
            );
        }
    }
}