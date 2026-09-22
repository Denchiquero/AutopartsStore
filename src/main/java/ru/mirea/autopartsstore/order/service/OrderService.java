package ru.mirea.autopartsstore.order.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
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
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final PartRepository partRepository;
    private final InventoryService inventoryService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            PartRepository partRepository,
            InventoryService inventoryService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.partRepository = partRepository;
        this.inventoryService = inventoryService;
    }

    private OrderResponse toResponse(
            CustomerOrder order
    ) {

        List<OrderItemResponse> items =
                orderItemRepository
                        .findByOrder_Id(order.getId())
                        .stream()
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
            CreateOrderRequest request
    ) {

        Customer customer =
                customerRepository.findById(request.customerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer with id "
                                                + request.customerId()
                                                + " not found"
                                )
                        );

        CustomerOrder order = new CustomerOrder();

        order.setCustomer(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {

            Part part =
                    partRepository.findById(itemRequest.partId())
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
            item.setQuantity(itemRequest.quantity());

            // Цена фиксируется на момент заказа
            item.setUnitPrice(part.getPrice());

            orderItemRepository.save(item);

            BigDecimal itemTotal =
                    part.getPrice().multiply(
                            BigDecimal.valueOf(
                                    itemRequest.quantity()
                            )
                    );

            totalPrice =
                    totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return toResponse(order);
    }
    public OrderResponse findById(Long id) {

        CustomerOrder order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order with id "
                                                + id
                                                + " not found"
                                )
                        );

        return toResponse(order);
    }

    public List<OrderResponse> findAll() {

        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order with id " + orderId + " not found"
                        )
                );

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            throw new InvalidOrderStateException(
                    "Order already has status " + newStatus
            );
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException(
                    "Cancelled order status cannot be changed"
            );
        }

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStateException(
                    "Completed order status cannot be changed"
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            returnItemsToStock(order);
        }

        if (newStatus == OrderStatus.COMPLETED
                && currentStatus != OrderStatus.CONFIRMED) {

            throw new InvalidOrderStateException(
                    "Only confirmed order can be completed"
            );
        }

        order.setStatus(newStatus);

        orderRepository.save(order);

        return toResponse(order);
    }
}