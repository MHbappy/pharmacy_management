package com.pharmacy.management.repository;

import com.pharmacy.management.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findAllByIsActiveAndNameContaining(Boolean isActive, String name, Pageable pageable);
    List<Company> findAllByIsActiveAndNameContaining(Boolean isActive, String name);
}
