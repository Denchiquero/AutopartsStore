package ru.mirea.autopartsstore.fitment.service;

import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationRequest;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.entity.VehicleApplication;
import ru.mirea.autopartsstore.fitment.repository.VehicleApplicationRepository;

import java.util.List;

@Service
public class VehicleApplicationService {

    private final VehicleApplicationRepository repository;

    public VehicleApplicationService(
            VehicleApplicationRepository repository
    ) {
        this.repository = repository;
    }

    public List<VehicleApplicationResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VehicleApplicationResponse findById(Long id) {
        return toResponse(getById(id));
    }

    public VehicleApplicationResponse create(
            VehicleApplicationRequest request
    ) {

        validateYears(request.yearFrom(), request.yearTo());

        VehicleApplication vehicle = new VehicleApplication();

        fillEntity(vehicle, request);

        return toResponse(repository.save(vehicle));
    }

    public VehicleApplicationResponse update(
            Long id,
            VehicleApplicationRequest request
    ) {

        validateYears(request.yearFrom(), request.yearTo());

        VehicleApplication vehicle = getById(id);

        fillEntity(vehicle, request);

        return toResponse(repository.save(vehicle));
    }

    public void delete(Long id) {
        VehicleApplication vehicle = getById(id);
        repository.delete(vehicle);
    }

    public VehicleApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle application with id "
                                        + id
                                        + " not found"
                        )
                );
    }

    private void fillEntity(
            VehicleApplication vehicle,
            VehicleApplicationRequest request
    ) {
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setGeneration(request.generation());

        vehicle.setYearFrom(request.yearFrom());
        vehicle.setYearTo(request.yearTo());

        vehicle.setEngineCode(request.engineCode());
        vehicle.setEngineVolume(request.engineVolume());
        vehicle.setPower(request.power());

        vehicle.setFuelType(request.fuelType());
        vehicle.setTransmission(request.transmission());
        vehicle.setDriveType(request.driveType());
        vehicle.setBodyType(request.bodyType());
    }

    private void validateYears(
            Integer yearFrom,
            Integer yearTo
    ) {
        if (yearTo != null && yearTo < yearFrom) {
            throw new IllegalArgumentException(
                    "yearTo cannot be less than yearFrom"
            );
        }
    }

    private VehicleApplicationResponse toResponse(
            VehicleApplication vehicle
    ) {
        return new VehicleApplicationResponse(
                vehicle.getId(),

                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getGeneration(),

                vehicle.getYearFrom(),
                vehicle.getYearTo(),

                vehicle.getEngineCode(),
                vehicle.getEngineVolume(),
                vehicle.getPower(),

                vehicle.getFuelType(),
                vehicle.getTransmission(),
                vehicle.getDriveType(),
                vehicle.getBodyType()
        );
    }
}