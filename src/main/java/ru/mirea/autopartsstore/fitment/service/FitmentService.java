package ru.mirea.autopartsstore.fitment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.catalog.service.PartService;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.fitment.dto.VehicleApplicationResponse;
import ru.mirea.autopartsstore.fitment.entity.PartFitment;
import ru.mirea.autopartsstore.fitment.entity.VehicleApplication;
import ru.mirea.autopartsstore.fitment.repository.PartFitmentRepository;
import ru.mirea.autopartsstore.fitment.repository.VehicleApplicationRepository;

import java.util.List;

@Service
public class FitmentService {

    private final PartFitmentRepository fitmentRepository;
    private final PartRepository partRepository;
    private final VehicleApplicationRepository vehicleRepository;
    private final PartService partService;

    public FitmentService(
            PartFitmentRepository fitmentRepository,
            PartRepository partRepository,
            VehicleApplicationRepository vehicleRepository,
            PartService partService
    ) {
        this.fitmentRepository = fitmentRepository;
        this.partRepository = partRepository;
        this.vehicleRepository = vehicleRepository;
        this.partService = partService;
    }

    @Transactional
    public void addFitment(
            Long partId,
            Long vehicleId
    ) {

        Part part = partRepository.findById(partId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Part with id " + partId + " not found"
                        )
                );

        VehicleApplication vehicle =
                vehicleRepository.findById(vehicleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vehicle application with id "
                                                + vehicleId
                                                + " not found"
                                )
                        );

        boolean alreadyExists =
                fitmentRepository
                        .existsByPart_IdAndVehicleApplication_Id(
                                partId,
                                vehicleId
                        );

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "This fitment already exists"
            );
        }

        PartFitment fitment = new PartFitment();

        fitment.setPart(part);
        fitment.setVehicleApplication(vehicle);

        fitmentRepository.save(fitment);
    }

    @Transactional
    public void removeFitment(
            Long partId,
            Long vehicleId
    ) {

        PartFitment fitment =
                fitmentRepository
                        .findByPart_IdAndVehicleApplication_Id(
                                partId,
                                vehicleId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Fitment not found"
                                )
                        );

        fitmentRepository.delete(fitment);
    }

    public List<VehicleApplicationResponse> findVehiclesForPart(
            Long partId
    ) {

        if (!partRepository.existsById(partId)) {
            throw new ResourceNotFoundException(
                    "Part with id " + partId + " not found"
            );
        }

        return fitmentRepository
                .findByPart_Id(partId)
                .stream()
                .map(PartFitment::getVehicleApplication)
                .map(this::toVehicleResponse)
                .toList();
    }

    public List<PartResponse> findPartsForVehicle(
            Long vehicleId
    ) {

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException(
                    "Vehicle application with id "
                            + vehicleId
                            + " not found"
            );
        }

        return fitmentRepository
                .findByVehicleApplication_Id(vehicleId)
                .stream()
                .map(PartFitment::getPart)
                .map(partService::toResponse)
                .toList();
    }

    private VehicleApplicationResponse toVehicleResponse(
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