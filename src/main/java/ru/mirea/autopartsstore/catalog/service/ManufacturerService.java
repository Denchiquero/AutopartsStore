package ru.mirea.autopartsstore.catalog.service;

import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerRequest;
import ru.mirea.autopartsstore.catalog.dto.ManufacturerResponse;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;
import ru.mirea.autopartsstore.catalog.repository.ManufacturerRepository;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final PartRepository partRepository;

    public ManufacturerService(
            ManufacturerRepository manufacturerRepository,
            PartRepository partRepository
    ) {
        this.manufacturerRepository = manufacturerRepository;
        this.partRepository = partRepository;
    }

    public List<ManufacturerResponse> findAll() {
        return manufacturerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ManufacturerResponse findById(Long id) {
        return toResponse(getById(id));
    }

    public ManufacturerResponse create(
            ManufacturerRequest request
    ) {

        if (manufacturerRepository
                .existsByNameIgnoreCase(request.name())) {

            throw new IllegalArgumentException(
                    "Manufacturer with this name already exists"
            );
        }

        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setName(request.name());
        manufacturer.setCountry(request.country());
        manufacturer.setWebsite(request.website());

        return toResponse(
                manufacturerRepository.save(manufacturer)
        );
    }

    public ManufacturerResponse update(
            Long id,
            ManufacturerRequest request
    ) {

        Manufacturer manufacturer = getById(id);

        manufacturer.setName(request.name());
        manufacturer.setCountry(request.country());
        manufacturer.setWebsite(request.website());

        return toResponse(
                manufacturerRepository.save(manufacturer)
        );
    }

    public void delete(Long id) {

        Manufacturer manufacturer = getById(id);

        if (partRepository.existsByManufacturer_Id(id)) {
            throw new IllegalStateException(
                    "Cannot delete manufacturer because parts use it"
            );
        }

        manufacturerRepository.delete(manufacturer);
    }

    private Manufacturer getById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manufacturer with id "
                                        + id
                                        + " not found"
                        )
                );
    }

    private ManufacturerResponse toResponse(
            Manufacturer manufacturer
    ) {
        return new ManufacturerResponse(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getCountry(),
                manufacturer.getWebsite()
        );
    }
}