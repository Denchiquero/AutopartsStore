package ru.mirea.autopartsstore.catalog.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.CreatePartRequest;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.service.PartService;
import ru.mirea.autopartsstore.common.dto.PageResponse;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public PageResponse<PartResponse> findAll(

            @RequestParam(required = false)
            Long manufacturerId,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ) {

        return partService.findAll(
                manufacturerId,
                categoryId,
                search,
                sortBy,
                direction,
                page,
                size
        );
    }

    @GetMapping("/{id}")
    public PartResponse findOne(@PathVariable Long id) {
        return partService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartResponse create(
            @Valid @RequestBody CreatePartRequest request
            ) {
        return partService.create(request);
    }

    @PutMapping("/{id}")
    public PartResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreatePartRequest request
    ) {
        return partService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        partService.delete(id);
    }

}