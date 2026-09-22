package ru.mirea.autopartsstore.fitment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle_application")
@Getter
@Setter
@NoArgsConstructor
public class VehicleApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String make;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(length = 100)
    private String generation;

    @Column(name = "year_from", nullable = false)
    private Integer yearFrom;

    @Column(name = "year_to")
    private Integer yearTo;

    @Column(name = "engine_code", length = 50)
    private String engineCode;

    @Column(name = "engine_volume", precision = 4, scale = 1)
    private BigDecimal engineVolume;

    private Integer power;

    @Column(name = "fuel_type", length = 30)
    private String fuelType;

    @Column(length = 30)
    private String transmission;

    @Column(name = "drive_type", length = 30)
    private String driveType;

    @Column(name = "body_type", length = 30)
    private String bodyType;
}