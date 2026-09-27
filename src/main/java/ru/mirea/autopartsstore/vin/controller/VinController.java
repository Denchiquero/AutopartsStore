package ru.mirea.autopartsstore.vin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.vin.dto.DecodedVin;
import ru.mirea.autopartsstore.vin.dto.VinPartsResponse;
import ru.mirea.autopartsstore.vin.service.VinService;



@RestController
@RequestMapping("/api/vin")
@Tag(
        name = "VIN",
        description = "Определитель VIN"
)

public class VinController {

    private final VinService vinService;

    public VinController(VinService vinService) {
        this.vinService = vinService;
    }

    @Operation(
            summary = "Декодировать VIN",
            description = "Определяет характеристики автомобиля по 17-символьному VIN-коду"
    )
    @GetMapping("/{vin}")
    public DecodedVin decode(@PathVariable String vin) {
        return vinService.decode(vin);
    }

    @Operation(
            summary = "Подобрать запчасти по VIN",
            description = "Декодирует VIN, определяет конфигурацию автомобиля и возвращает совместимые запчасти"
    )
    @GetMapping("/{vin}/parts")
    public VinPartsResponse findParts(
            @PathVariable String vin
    ) {
        return vinService.findParts(vin);
    }


}