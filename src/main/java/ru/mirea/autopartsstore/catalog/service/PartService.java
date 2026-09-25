package ru.mirea.autopartsstore.catalog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.entity.Part;
import ru.mirea.autopartsstore.catalog.repository.ManufacturerRepository;
import ru.mirea.autopartsstore.catalog.repository.PartCategoryRepository;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.catalog.dto.CreatePartRequest;
import ru.mirea.autopartsstore.catalog.dto.PartResponse;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;
import ru.mirea.autopartsstore.common.dto.PageResponse;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.inventory.entity.Stock;
import ru.mirea.autopartsstore.inventory.repository.StockRepository;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final PartCategoryRepository categoryRepository;
    private final StockRepository stockRepository;

    public PartService(
            PartRepository partRepository,
            ManufacturerRepository manufacturerRepository,
            PartCategoryRepository categoryRepository,
            StockRepository stockRepository
    ) {
        this.partRepository = partRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.categoryRepository = categoryRepository;
        this.stockRepository = stockRepository;
    }

    private String getSortProperty(String sortBy) {

        return switch (sortBy.toLowerCase()) {

            case "id" -> "id";

            case "name" -> "name";

            case "price" -> "price";

            case "sku" -> "sku";

            case "article" -> "article";

            case "manufacturer" -> "manufacturer.name";

            case "category" -> "category.name";

            default -> throw new IllegalArgumentException(
                    "Unsupported sort field: " + sortBy
            );
        };
    }


    public PageResponse<PartResponse> findAll(
            Long manufacturerId,
            Long categoryId,
            String search,
            String sortBy,
            String direction,
            int page,
            int size
    ) {

        if (search == null) {
            search = "";
        } else {
            search = search.trim();
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        String sortProperty = getSortProperty(sortBy);

        Sort.Direction sortDirection;

        if ("asc".equalsIgnoreCase(direction)) {

            sortDirection = Sort.Direction.ASC;

        } else if ("desc".equalsIgnoreCase(direction)) {

            sortDirection = Sort.Direction.DESC;

        } else {

            throw new IllegalArgumentException(
                    "Direction must be asc or desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        sortDirection,
                        sortProperty
                )
        );

        Page<Part> result =
                partRepository.findFiltered(
                        manufacturerId,
                        categoryId,
                        search,
                        pageable
                );

        List<PartResponse> content =
                toResponses(result.getContent());

        return new PageResponse<>(
                content,

                result.getNumber(),
                result.getSize(),

                result.getTotalElements(),
                result.getTotalPages(),

                result.isFirst(),
                result.isLast()
        );
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

        Integer stockQuantity =
                stockRepository.findById(part.getId())
                        .map(Stock::getQuantity)
                        .orElse(0);

        return toResponse(
                part,
                stockQuantity
        );
    }

    private PartResponse toResponse(
            Part part,
            Integer stockQuantity
    ) {

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
                part.getCategory().getName(),

                stockQuantity
        );
    }

    public List<PartResponse> toResponses(
            List<Part> parts
    ) {

        if (parts.isEmpty()) {
            return List.of();
        }

        List<Long> partIds = parts.stream()
                .map(Part::getId)
                .toList();

        Map<Long, Integer> stockByPartId =
                stockRepository
                        .findByPartIdIn(partIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Stock::getPartId,
                                        Stock::getQuantity
                                )
                        );

        return parts.stream()
                .map(part ->
                        toResponse(
                                part,
                                stockByPartId.getOrDefault(
                                        part.getId(),
                                        0
                                )
                        )
                )
                .toList();
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