package ru.mirea.autopartsstore.fitment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationRequest;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.service.VehicleApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleApplicationController {

    private final VehicleApplicationService service;

    public VehicleApplicationController(
            VehicleApplicationService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleApplicationResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public VehicleApplicationResponse findById(
            @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleApplicationResponse create(
            @Valid @RequestBody VehicleApplicationRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public VehicleApplicationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleApplicationRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        service.delete(id);
    }
}