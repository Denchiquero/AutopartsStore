package ru.mirea.autopartsstore.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerRequest;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerResponse;
import ru.mirea.autopartsstore.catalog.service.ManufacturerService;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
@Tag(
        name = "Manufacturers",
        description = "Список производителей"
)

public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(
            ManufacturerService manufacturerService
    ) {
        this.manufacturerService = manufacturerService;
    }

    @Operation(summary = "Получить производителей")
    @GetMapping
    public List<ManufacturerResponse> findAll() {
        return manufacturerService.findAll();
    }

    @Operation(summary = "Получить производителя по ID")
    @GetMapping("/{id}")
    public ManufacturerResponse findById(
            @PathVariable Long id
    ) {
        return manufacturerService.findById(id);
    }

    @Operation(summary = "Создать производителя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManufacturerResponse create(
            @Valid @RequestBody ManufacturerRequest request
    ) {
        return manufacturerService.create(request);
    }

    @Operation(summary = "Изменить производителя")
    @PutMapping("/{id}")
    public ManufacturerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ManufacturerRequest request
    ) {
        return manufacturerService.update(id, request);
    }

    @Operation(summary = "Изменить производителя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        manufacturerService.delete(id);
    }
}
