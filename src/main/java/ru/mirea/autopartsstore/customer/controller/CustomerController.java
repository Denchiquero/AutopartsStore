package ru.mirea.autopartsstore.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.customer.dto.CustomerRequest;
import ru.mirea.autopartsstore.customer.dto.CustomerResponse;
import ru.mirea.autopartsstore.customer.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customers",
        description = "Список клиентов"
)
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }

    @Operation(summary = "Получить покупателей")
    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @Operation(summary = "Получить покупателя по ID")
    @GetMapping("/{id}")
    public CustomerResponse findById(
            @PathVariable Long id
    ) {
        return customerService.findById(id);
    }

    @Operation(summary = "Создать покупателя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerService.create(request);
    }

    @Operation(summary = "Изменить покупателя")
    @PutMapping("/{id}")
    public CustomerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerService.update(id, request);
    }

    @Operation(summary = "Удалить покупателя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        customerService.delete(id);
    }
}