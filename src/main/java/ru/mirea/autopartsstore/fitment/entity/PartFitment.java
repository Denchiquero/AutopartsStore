package ru.mirea.autopartsstore.fitment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mirea.autopartsstore.catalog.entity.Part;

@Entity
@Table(
        name = "part_fitment",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "part_id",
                                "vehicle_application_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PartFitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne
    @JoinColumn(
            name = "vehicle_application_id",
            nullable = false
    )
    private VehicleApplication vehicleApplication;
}