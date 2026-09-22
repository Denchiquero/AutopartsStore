package ru.mirea.autopartsstore.fitment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.service.FitmentService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FitmentController {

    private final FitmentService fitmentService;

    public FitmentController(FitmentService fitmentService) {
        this.fitmentService = fitmentService;
    }

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

    @GetMapping(
            "/parts/{partId}/fitments"
    )
    public List<VehicleApplicationResponse> findVehiclesForPart(
            @PathVariable Long partId
    ) {
        return fitmentService.findVehiclesForPart(partId);
    }

    @GetMapping(
            "/vehicles/{vehicleId}/parts"
    )
    public List<PartResponse> findPartsForVehicle(
            @PathVariable Long vehicleId
    ) {
        return fitmentService.findPartsForVehicle(vehicleId);
    }
}