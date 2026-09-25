package ru.mirea.autopartsstore.fitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.fitment.entity.PartFitment;

import java.util.List;
import java.util.Optional;

public interface PartFitmentRepository
        extends JpaRepository<PartFitment, Long> {

    boolean existsByPart_IdAndVehicleApplication_Id(
            Long partId,
            Long vehicleApplicationId
    );

    Optional<PartFitment> findByPart_IdAndVehicleApplication_Id(
            Long partId,
            Long vehicleApplicationId
    );

    List<PartFitment> findByPart_Id(Long partId);

    @Query("""
        SELECT pf
        FROM PartFitment pf
        JOIN FETCH pf.part p
        JOIN FETCH p.manufacturer
        JOIN FETCH p.category
        WHERE pf.vehicleApplication.id = :vehicleId
        """)
    List<PartFitment> findByVehicleApplication_Id(
            @Param("vehicleId") Long vehicleId
    );
}