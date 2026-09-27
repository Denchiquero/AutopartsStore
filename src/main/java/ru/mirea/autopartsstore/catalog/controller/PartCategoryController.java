package ru.mirea.autopartsstore.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryRequest;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryResponse;
import ru.mirea.autopartsstore.catalog.service.PartCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(
        name = "Categories",
        description = "Список категорий автозапчастей"
)

public class PartCategoryController {

    private final PartCategoryService categoryService;

    public PartCategoryController(
            PartCategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Получить категории запчастей")
    @GetMapping
    public List<PartCategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Получить категории запчастей по ID")
    @GetMapping("/{id}")
    public PartCategoryResponse findById(
            @PathVariable Long id
    ) {
        return categoryService.findById(id);
    }

    @Operation(summary = "Создать категорию")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartCategoryResponse create(
            @Valid @RequestBody PartCategoryRequest request
    ) {
        return categoryService.create(request);
    }

    @Operation(summary = "Изменить категорию")
    @PutMapping("/{id}")
    public PartCategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PartCategoryRequest request
    ) {
        return categoryService.update(id, request);
    }

    @Operation(summary = "Удалить категорию")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);
    }
}