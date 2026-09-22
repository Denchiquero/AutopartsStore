package ru.mirea.autopartsstore.catalog.service;

import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.ManufacturerRepository;
import ru.mirea.autopartsstore.catalog.repository.PartCategoryRepository;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.catalog.dto.CreatePartRequest;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;


import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final PartCategoryRepository categoryRepository;

    public PartService(
            PartRepository partRepository,
            ManufacturerRepository manufacturerRepository,
            PartCategoryRepository categoryRepository
    ) {
        this.partRepository = partRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.categoryRepository = categoryRepository;
    }


    public List<PartResponse> findAll() {
        return partRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public PartResponse findById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Part with id " + id + " not found"
                        )
                );

        return toResponse(part);
    }


    public PartResponse create(CreatePartRequest request) {

        Manufacturer manufacturer =
                manufacturerRepository.findById(request.manufacturerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Manufacturer with id "
                                                + request.manufacturerId()
                                                + " not found"
                                )
                        );

        PartCategory category =
                categoryRepository.findById(request.categoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category with id "
                                                + request.categoryId()
                                                + " not found"
                                )
                        );

        Part part = new Part();

        part.setName(request.name());
        part.setSku(request.sku());
        part.setArticle(request.article());
        part.setDescription(request.description());
        part.setPrice(request.price());

        part.setManufacturer(manufacturer);
        part.setCategory(category);

        Part savedPart = partRepository.save(part);

        return toResponse(savedPart);
    }


    public PartResponse toResponse(Part part) {

        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getSku(),
                part.getArticle(),
                part.getDescription(),
                part.getPrice(),

                part.getManufacturer().getId(),
                part.getManufacturer().getName(),

                part.getCategory().getId(),
                part.getCategory().getName()
        );
    }

    public PartResponse update(Long id, CreatePartRequest request) {

        Part part = partRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Part with id " + id + " not found"
                        )
                );

        Manufacturer manufacturer =
                manufacturerRepository.findById(request.manufacturerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Manufacturer with id "
                                                + request.manufacturerId()
                                                + " not found"
                                )
                        );

        PartCategory category =
                categoryRepository.findById(request.categoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category with id "
                                                + request.categoryId()
                                                + " not found"
                                )
                        );

        part.setName(request.name());
        part.setSku(request.sku());
        part.setArticle(request.article());
        part.setDescription(request.description());
        part.setPrice(request.price());
        part.setManufacturer(manufacturer);
        part.setCategory(category);

        Part updatedPart = partRepository.save(part);

        return toResponse(updatedPart);
    }

    public void delete(Long id) {

        if (!partRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Part with id " + id + " not found"
            );
        }

        partRepository.deleteById(id);
    }
}