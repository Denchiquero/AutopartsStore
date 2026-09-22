package ru.mirea.autopartsstore.order.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderResponse;
import ru.mirea.autopartsstore.order.dto.UpdateOrderStatusRequest;
import ru.mirea.autopartsstore.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(
            @PathVariable Long id
    ) {
        return orderService.findById(id);
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll();
    }

    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.changeStatus(
                id,
                request.status()
        );
    }
}
