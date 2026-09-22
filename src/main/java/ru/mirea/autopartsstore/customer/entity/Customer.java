package ru.mirea.autopartsstore.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "customer")
@Getter
@Setter
@NoArgsConstructor

public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 150)
    private String name;

    @Column (length = 30)
    private String phone;

    @Column (unique = true)
    private String email;

}