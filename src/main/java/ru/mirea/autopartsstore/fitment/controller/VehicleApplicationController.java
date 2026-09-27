package ru.mirea.autopartsstore.fitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationRequest;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.service.VehicleApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(
        name = "Vehicle fitments",
        description = "Список конфигураций авто"
)

public class VehicleApplicationController {

    private final VehicleApplicationService service;

    public VehicleApplicationController(
            VehicleApplicationService service
    ) {
        this.service = service;
    }

    @Operation(summary = "Получить конфигурации автомобилей")
    @GetMapping
    public List<VehicleApplicationResponse> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Получить конфигурацию автомобиля по ID")
    @GetMapping("/{id}")
    public VehicleApplicationResponse findById(
            @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @Operation(summary = "Создать конфигурацию автомобиля")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleApplicationResponse create(
            @Valid @RequestBody VehicleApplicationRequest request
    ) {
        return service.create(request);
    }

    @Operation(summary = "Изменить конфигурацию автомобиля")
    @PutMapping("/{id}")
    public VehicleApplicationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleApplicationRequest request
    ) {
        return service.update(id, request);
    }

    @Operation(summary = "Удалить конфигурацию автомобиля")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        service.delete(id);
    }
}