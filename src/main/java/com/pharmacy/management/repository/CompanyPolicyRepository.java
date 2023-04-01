package com.pharmacy.management.repository;

import com.pharmacy.management.model.CompanyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy, Long> {
    List<CompanyPolicy> findAllByIsActive(Boolean isActive);
    List<CompanyPolicy> findAllByCompany_IdAndIsActive(Long companyId, Boolean isActive);
}
