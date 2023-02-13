package com.pharmacy.management.repository;

import com.pharmacy.management.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByIsActive(Boolean isActive);
}
