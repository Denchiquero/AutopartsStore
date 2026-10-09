package ru.mirea.autopartsstore.vin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import ru.mirea.autopartsstore.vin.dto.VinResponse;
import ru.mirea.autopartsstore.vin.service.VinService;

@RestController
@RequestMapping("/api/vin")
@Tag(
        name = "VIN",
        description = "Определитель VIN"
)
public class VinController {

    private final VinService vinService;

    public VinController(
            VinService vinService
    ) {
        this.vinService = vinService;
    }

    @Operation(
            summary = "Декодировать VIN",
            description = "Определяет характеристики автомобиля и ищет соответствующую конфигурацию в базе"
    )
    @GetMapping("/{vin}")
    public VinResponse decode(
            @PathVariable String vin
    ) {
        return vinService.decodeWithVehicle(vin);
    }
}