package ru.mirea.autopartsstore.fitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.service.FitmentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(
        name = "Fitments",
        description = "Список совместимых деталей"
)

public class FitmentController {

    private final FitmentService fitmentService;

    public FitmentController(FitmentService fitmentService) {
        this.fitmentService = fitmentService;
    }

    @Operation(
            summary = "Добавить совместимость запчасти",
            description = "Связывает запчасть с конфигурацией автомобиля"
    )
    @PostMapping(
            "/parts/{partId}/fitments/{vehicleId}"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void addFitment(
            @PathVariable Long partId,
            @PathVariable Long vehicleId
    ) {
        fitmentService.addFitment(
                partId,
                vehicleId
        );
    }

    @Operation(summary = "Удалить совместимость запчасти")
    @DeleteMapping(
            "/parts/{partId}/fitments/{vehicleId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFitment(
            @PathVariable Long partId,
            @PathVariable Long vehicleId
    ) {
        fitmentService.removeFitment(
                partId,
                vehicleId
        );
    }

    @Operation(
            summary = "Получить совместимые автомобили",
            description = "Возвращает конфигурации автомобилей, совместимые с указанной запчастью"
    )
    @GetMapping(
            "/parts/{partId}/fitments"
    )
    public List<VehicleApplicationResponse> findVehiclesForPart(
            @PathVariable Long partId
    ) {
        return fitmentService.findVehiclesForPart(partId);
    }

    @Operation(
            summary = "Получить совместимые запчасти",
            description = "Возвращает запчасти, совместимые с указанной конфигурацией автомобиля"
    )
    @GetMapping(
            "/vehicles/{vehicleId}/parts"
    )
    public List<PartResponse> findPartsForVehicle(
            @PathVariable Long vehicleId
    ) {
        return fitmentService.findPartsForVehicle(vehicleId);
    }
}