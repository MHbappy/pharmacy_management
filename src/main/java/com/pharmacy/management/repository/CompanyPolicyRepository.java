package com.pharmacy.management.repository;

import com.pharmacy.management.model.Company;
import com.pharmacy.management.model.CompanyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy, Long> {
    List<CompanyPolicy> findAllByIsActive(Boolean isActive);
    List<CompanyPolicy> findAllByCompany_IdAndIsActive(Long companyId, Boolean isActive);
    Optional<CompanyPolicy> findByDesignationAndCompanyAndIsActive(String designation, Company company, Boolean isActive);
    Optional<CompanyPolicy> findByNameAndIsActive(String name, Boolean isActive);

}
