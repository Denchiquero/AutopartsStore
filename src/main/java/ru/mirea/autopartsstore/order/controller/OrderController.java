package ru.mirea.autopartsstore.order.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderResponse;
import ru.mirea.autopartsstore.order.dto.UpdateOrderStatusRequest;
import ru.mirea.autopartsstore.order.entity.OrderStatus;
import ru.mirea.autopartsstore.order.service.OrderService;

import java.time.LocalDate;
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
    public PageResponse<OrderResponse> findAll(

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(required = false)
            Long customerId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ) {
        return orderService.findAll(
                status,
                customerId,
                from,
                to,
                page,
                size
        );
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
