package ru.mirea.autopartsstore.fitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

    List<PartFitment> findByVehicleApplication_Id(
            Long vehicleApplicationId
    );
}