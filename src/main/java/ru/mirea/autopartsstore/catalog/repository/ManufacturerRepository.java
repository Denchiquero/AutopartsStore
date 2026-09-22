package ru.mirea.autopartsstore.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.catalog.entity.Manufacturer;

public interface ManufacturerRepository
        extends JpaRepository<Manufacturer, Long> {
}