package ru.mirea.autopartsstore.fitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.fitment.entity.VehicleApplication;

import java.util.List;

public interface VehicleApplicationRepository
        extends JpaRepository<VehicleApplication, Long> {
    @Query("""
        SELECT v
        FROM VehicleApplication v
        WHERE LOWER(v.make) = LOWER(:make)
          AND LOWER(v.model) = LOWER(:model)
          AND LOWER(v.generation) = LOWER(:generation)
          AND v.yearFrom <= :year
          AND (v.yearTo IS NULL OR v.yearTo >= :year)
          AND LOWER(v.engineCode) = LOWER(:engineCode)
          AND LOWER(v.transmission) = LOWER(:transmission)
          AND LOWER(v.driveType) = LOWER(:driveType)
          AND LOWER(v.bodyType) = LOWER(:bodyType)
        """)
    List<VehicleApplication> findMatchingVehicle(
            @Param("make") String make,
            @Param("model") String model,
            @Param("generation") String generation,
            @Param("year") Integer year,
            @Param("engineCode") String engineCode,
            @Param("transmission") String transmission,
            @Param("driveType") String driveType,
            @Param("bodyType") String bodyType
    );
}