package ru.mirea.autopartsstore.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.order.dto.CreateOrderRequest;
import ru.mirea.autopartsstore.order.dto.OrderResponse;
import ru.mirea.autopartsstore.order.dto.UpdateOrderStatusRequest;
import ru.mirea.autopartsstore.order.entity.OrderStatus;
import ru.mirea.autopartsstore.order.service.OrderService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/orders")
@Tag(
        name = "Orders",
        description = "Управление заказами"
)
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Получить заказы",
            description = "Поддерживает фильтрацию по статусу, покупателю и дате, а также пагинацию"
    )
    @GetMapping
    public PageResponse<OrderResponse> findAll(

            Authentication authentication,

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
                authentication.getName(),
                status,
                customerId,
                from,
                to,
                page,
                size
        );
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{id}")
    public OrderResponse findById(
            Authentication authentication,
            @PathVariable Long id
    ) {

        return orderService.findById(
                authentication.getName(),
                id
        );
    }

    @Operation(
            summary = "Создать заказ",
            description = "Создаёт заказ текущего пользователя и списывает заказанные товары со склада"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return orderService.create(
                authentication.getName(),
                request
        );
    }

    @Operation(
            summary = "Изменить статус заказа",
            description = """
                Допустимые переходы:
                CREATED → CONFIRMED или CANCELLED
                CONFIRMED → COMPLETED или CANCELLED
                """
    )
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
