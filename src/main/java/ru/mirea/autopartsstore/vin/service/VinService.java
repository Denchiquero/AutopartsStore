package ru.mirea.autopartsstore.vin.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.catalog.service.PartService;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.fitment.entity.VehicleApplication;
import ru.mirea.autopartsstore.fitment.repository.VehicleApplicationRepository;
import ru.mirea.autopartsstore.fitment.service.FitmentService;
import ru.mirea.autopartsstore.vin.dto.DecodedVin;
import ru.mirea.autopartsstore.vin.dto.VinPartsResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class VinService {

    private final JsonNode catalog;
//    private final PartRepository partRepository;
//    private final PartService partService;
    private final VehicleApplicationRepository vehicleRepository;
    private final FitmentService fitmentService;

    public VinService(
            ObjectMapper objectMapper,
            VehicleApplicationRepository vehicleRepository,
            FitmentService fitmentService
    ) throws IOException {
        this.catalog = objectMapper.readTree(
                new ClassPathResource("vin-catalog.json").getInputStream()
        );

        this.vehicleRepository = vehicleRepository;
        this.fitmentService = fitmentService;
    }

    public DecodedVin decode(String vin) {
        if (vin == null || vin.length() != 17) {
            throw new IllegalArgumentException(
                    "VIN must contain 17 characters"
            );
        }

        vin = vin.toUpperCase();

        String makeCode = vin.substring(0, 3);
        String modelCode = vin.substring(3, 6);
        String engineCode = vin.substring(6, 8);

        String transmissionCode = vin.substring(8, 9);
        String yearCode = vin.substring(9, 10);
        String driveCode = vin.substring(10, 11);
        String bodyCode = vin.substring(11, 12);

        String make = catalog
                .path("makes")
                .path(makeCode)
                .asText(null);

        JsonNode model = catalog
                .path("models")
                .path(makeCode)
                .path(modelCode);

        JsonNode engine = catalog
                .path("engines")
                .path(makeCode)
                .path(engineCode);

        String transmission = catalog
                .path("transmissions")
                .path(transmissionCode)
                .asText(null);

        int year = catalog
                .path("years")
                .path(yearCode)
                .asInt();

        String drive = catalog
                .path("drives")
                .path(driveCode)
                .asText(null);

        String body = catalog
                .path("bodies")
                .path(bodyCode)
                .asText(null);

        if (make == null || model.isMissingNode() || engine.isMissingNode()) {
            throw new IllegalArgumentException(
                    "VIN contains unknown vehicle codes"
            );
        }

        return new DecodedVin(
                vin,
                make,
                model.path("name").asText(),
                model.path("generation").asText(),
                year,
                engine.path("code").asText(),
                new BigDecimal(engine.path("volume").asText()),
                engine.path("power").asInt(),
                engine.path("fuelType").asText(),
                transmission,
                drive,
                body
        );
    }


    public VinPartsResponse findParts(String vin) {

        DecodedVin decodedVin = decode(vin);

        List<VehicleApplication> matches =
                vehicleRepository.findMatchingVehicle(
                        decodedVin.make(),
                        decodedVin.model(),
                        decodedVin.generation(),
                        decodedVin.modelYear(),
                        decodedVin.engineCode(),
                        decodedVin.transmission(),
                        decodedVin.driveType(),
                        decodedVin.bodyType()
                );

        if (matches.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Vehicle configuration not found in catalog"
            );
        }

        if (matches.size() > 1) {
            throw new IllegalStateException(
                    "More than one vehicle configuration matches VIN"
            );
        }

        VehicleApplication vehicle = matches.getFirst();

        List<PartResponse> parts =
                fitmentService.findPartsForVehicle(
                        vehicle.getId()
                );

        return new VinPartsResponse(
                decodedVin,
                vehicle.getId(),
                parts
        );
    }
}