package ru.mirea.autopartsstore.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.catalog.entity.Part;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    @Query("""
        SELECT p
        FROM Part p
        WHERE (:manufacturerId IS NULL
               OR p.manufacturer.id = :manufacturerId)

          AND (:categoryId IS NULL
               OR p.category.id = :categoryId)

          AND (
               :search = ''
               OR LOWER(p.name)
                    LIKE CONCAT('%', LOWER(:search), '%')

               OR LOWER(p.sku)
                    LIKE CONCAT('%', LOWER(:search), '%')

               OR LOWER(p.article)
                    LIKE CONCAT('%', LOWER(:search), '%')
          )
        """)
    @EntityGraph(attributePaths = {
            "manufacturer",
            "category"
    })
    Page<Part> findFiltered(
            @Param("manufacturerId") Long manufacturerId,
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsByManufacturer_Id(Long manufacturerId);

    boolean existsByCategory_Id(Long categoryId);
}