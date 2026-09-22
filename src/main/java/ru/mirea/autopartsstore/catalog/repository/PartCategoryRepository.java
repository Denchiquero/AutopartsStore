package ru.mirea.autopartsstore.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.catalog.entity.PartCategory;

public interface PartCategoryRepository
        extends JpaRepository<PartCategory, Long> {
}