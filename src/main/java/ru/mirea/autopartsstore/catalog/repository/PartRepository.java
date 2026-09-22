package ru.mirea.autopartsstore.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.catalog.entity.Part;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findBySkuIn(List<String> skus);
}