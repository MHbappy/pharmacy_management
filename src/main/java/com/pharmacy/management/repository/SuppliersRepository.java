package com.pharmacy.management.repository;

import com.pharmacy.management.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Suppliers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {

    Page<Suppliers> findAllByIsActiveAndCompanyNameContaining(Boolean isActive, String companyName, Pageable pageable);
    List<Suppliers> findAllByIsActiveAndCompanyNameContaining(Boolean isActive, String companyName);
    Optional<Suppliers> findByCompanyNameAndIsActive(String companyName, Boolean isActive);
    Boolean existsByCompanyNameAndIsActive(String companyName, Boolean isActive);
}
