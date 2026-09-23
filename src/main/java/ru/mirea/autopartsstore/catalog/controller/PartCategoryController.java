package ru.mirea.autopartsstore.catalog.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryRequest;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryResponse;
import ru.mirea.autopartsstore.catalog.service.PartCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class PartCategoryController {

    private final PartCategoryService categoryService;

    public PartCategoryController(
            PartCategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<PartCategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public PartCategoryResponse findById(
            @PathVariable Long id
    ) {
        return categoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartCategoryResponse create(
            @Valid @RequestBody PartCategoryRequest request
    ) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public PartCategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PartCategoryRequest request
    ) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);
    }
}