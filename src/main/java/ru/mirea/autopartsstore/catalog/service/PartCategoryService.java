package ru.mirea.autopartsstore.catalog.service;

import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryRequest;
import ru.mirea.autopartsstore.catalog.dto.PartCategoryResponse;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;
import ru.mirea.autopartsstore.catalog.repository.PartCategoryRepository;
import ru.mirea.autopartsstore.catalog.repository.PartRepository;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class PartCategoryService {

    private final PartCategoryRepository categoryRepository;
    private final PartRepository partRepository;

    public PartCategoryService(
            PartCategoryRepository categoryRepository,
            PartRepository partRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.partRepository = partRepository;
    }

    public List<PartCategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PartCategoryResponse findById(Long id) {
        return toResponse(getById(id));
    }

    public PartCategoryResponse create(
            PartCategoryRequest request
    ) {

        if (categoryRepository
                .existsByNameIgnoreCase(request.name())) {

            throw new IllegalArgumentException(
                    "Category with this name already exists"
            );
        }

        PartCategory category = new PartCategory();

        category.setName(request.name());
        category.setDescription(request.description());

        return toResponse(
                categoryRepository.save(category)
        );
    }

    public PartCategoryResponse update(
            Long id,
            PartCategoryRequest request
    ) {

        PartCategory category = getById(id);

        category.setName(request.name());
        category.setDescription(request.description());

        return toResponse(
                categoryRepository.save(category)
        );
    }

    public void delete(Long id) {

        PartCategory category = getById(id);

        if (partRepository.existsByCategory_Id(id)) {
            throw new IllegalStateException(
                    "Cannot delete category because parts use it"
            );
        }

        categoryRepository.delete(category);
    }

    private PartCategory getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id "
                                        + id
                                        + " not found"
                        )
                );
    }

    private PartCategoryResponse toResponse(
            PartCategory category
    ) {
        return new PartCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}