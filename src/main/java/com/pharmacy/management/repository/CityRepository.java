package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Page<City> findAllByIsActiveAndNameContaining(Boolean isActive, String name, Pageable pageable);
    List<City> findAllByIsActiveAndNameContaining(Boolean isActive, String name);
    Optional<City> findByNameAndIsActive(String name, Boolean isActive);
}
