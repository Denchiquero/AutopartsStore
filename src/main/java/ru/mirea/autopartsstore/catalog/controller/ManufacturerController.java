package ru.mirea.autopartsstore.catalog.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerRequest;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerResponse;
import ru.mirea.autopartsstore.catalog.service.ManufacturerService;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(
            ManufacturerService manufacturerService
    ) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
    public List<ManufacturerResponse> findAll() {
        return manufacturerService.findAll();
    }

    @GetMapping("/{id}")
    public ManufacturerResponse findById(
            @PathVariable Long id
    ) {
        return manufacturerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManufacturerResponse create(
            @Valid @RequestBody ManufacturerRequest request
    ) {
        return manufacturerService.create(request);
    }

    @PutMapping("/{id}")
    public ManufacturerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ManufacturerRequest request
    ) {
        return manufacturerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        manufacturerService.delete(id);
    }
}
