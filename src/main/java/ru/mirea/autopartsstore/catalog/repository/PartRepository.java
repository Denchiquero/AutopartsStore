package ru.mirea.autopartsstore.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.catalog.entity.Part;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findBySkuIn(List<String> skus);

    List<Part> findByManufacturer_Id(Long manufacturerId);

    List<Part> findByCategory_Id(Long categoryId);

    List<Part> findByManufacturer_IdAndCategory_Id(
            Long manufacturerId,
            Long categoryId
    );

    boolean existsByManufacturer_Id(Long manufacturerId);

    boolean existsByCategory_Id(Long categoryId);
}