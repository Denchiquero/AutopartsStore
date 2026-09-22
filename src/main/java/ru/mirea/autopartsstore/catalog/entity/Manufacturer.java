package ru.mirea.autopartsstore.catalog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name = "manufacturer")
public class Manufacturer {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true, length = 100)
    private String name;

    @Column (length = 100)
    private String country;

    @Column (length = 255)
    private String website;
}
