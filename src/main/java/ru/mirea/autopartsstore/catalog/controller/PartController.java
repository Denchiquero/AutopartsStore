package ru.mirea.autopartsstore.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.CreatePartRequest;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.service.PartService;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/parts")
@Tag(
        name = "Parts",
        description = "Каталог автозапчастей"
)

public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @Operation(
            summary = "Получить каталог запчастей",
            description = "Поиск, фильтрация, сортировка и пагинация"
    )
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

    @Operation(summary = "Получить запчасть по ID")
    @GetMapping("/{id}")
    public PartResponse findOne(@PathVariable Long id) {
        return partService.findById(id);
    }

    @Operation(summary = "Создать запчасть")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartResponse create(
            @Valid @RequestBody CreatePartRequest request
            ) {
        return partService.create(request);
    }

    @Operation(summary = "Изменить запчасть")
    @PutMapping("/{id}")
    public PartResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreatePartRequest request
    ) {
        return partService.update(id, request);
    }

    @Operation(summary = "Удалить запчасть")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        partService.delete(id);
    }

}